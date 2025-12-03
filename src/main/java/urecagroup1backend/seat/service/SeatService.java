package urecagroup1backend.seat.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import urecagroup1backend.common.lock.DistributedLock;
import urecagroup1backend.seat.domain.SeatStatus;
import urecagroup1backend.seat.dto.SeatReservationResponse;
import urecagroup1backend.seat.dto.SeatResponse;
import urecagroup1backend.seat.domain.Seat;
import urecagroup1backend.seat.domain.SeatReservation;
import urecagroup1backend.seat.repository.SeatReservationRepository;
import urecagroup1backend.seat.repository.SeatRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeatService {
    private final SeatRepository seatRepository;
    private final SeatReservationRepository seatReservationRepository;
    private final DistributedLock distributedLock; // 분산 락을 위해 구현된 객체

    // 전체 좌석 조회
    @Transactional
    public List<SeatResponse> getAllSeats() {
        // 현재 활성화되어있는 예약 내역 가져오기
        List<SeatReservation> seatReservationList = seatReservationRepository.findAllByIsDeleted(false);

        LocalDateTime now = LocalDateTime.now();

        for(SeatReservation sr : seatReservationList) {
            Duration duration = Duration.between(sr.getCreatedAt(), now);

            if(duration.toMinutes() >= 10) {
                // 예약 내역 논리 삭제
                sr.delete();

                Seat seat = seatRepository.findById(sr.getSeatId()).orElseThrow(
                        () -> new IllegalArgumentException("좌석을 찾을 수 없습니다.")
                );

                // 좌석 예약 가능한 상태로 변경
                seat.empty();
            }
        }
        List<Seat> seats = seatRepository.findAll();

        List<SeatResponse> seatResponseList = new ArrayList<>();

        for(Seat s : seats) {
            seatResponseList.add(SeatResponse.from(s));
        }

        return seatResponseList;
    }

    // 나의 전체 예약 내역 조회
    @Transactional
    public List<SeatReservationResponse> getMySeatReservation(Long userId) {
        // 현재 좌석의 예약 내역 확인
        List<SeatReservation> seatReservationList = seatReservationRepository.findByUserId(userId);

        List<SeatReservationResponse> seatReservationResponseList = new ArrayList<>();

        for(SeatReservation sr : seatReservationList) {
            seatReservationResponseList.add(SeatReservationResponse.from(sr));
        }

        return seatReservationResponseList;
    }

    // 예약 시도 (Redis 분산 Lock 적용)
    @Transactional
    public SeatReservationResponse tryReserveSeat(Long seatId, Long userId) {
        String lockKey = "seat: " + seatId;

        return distributedLock.executeWithLock(lockKey, 5, 10, () -> {
            return reserveSeat(seatId, userId);
        });
    }

    // 예약
    @Transactional
    public SeatReservationResponse reserveSeat(Long seatId, Long userId) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new IllegalArgumentException("좌석을 찾을 수 없습니다."));

        // 내가 이미 예약 or 입실 중인지 확인
        validateUserNotAlreadyReservedOrUsing(userId);

        if(seat.getSeatStatus() == SeatStatus.RESERVED) {
            throw new IllegalStateException("이미 예약된 좌석입니다.");
        }

        if(seat.getSeatStatus() == SeatStatus.USED) {
            throw new IllegalStateException("이미 입실 중인 좌석입니다.");
        }

        SeatReservation seatReservation = SeatReservation.builder()
                .seatId(seat.getId())
                .userId(userId)
                .build();

        SeatReservation saved = seatReservationRepository.save(seatReservation);

        // 예약 성공했으면 좌석 상태 RESERVED로 변경
        if(!saved.getIsDeleted()) {
            seat.reserve();
        }

        return SeatReservationResponse.from(saved);
    }

    // 예약 취소
    @Transactional
    public SeatReservationResponse cancelReservation(Long seatId, Long userId) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new IllegalArgumentException("좌석을 찾을 수 없습니다."));

        if(seat.getSeatStatus() == SeatStatus.EMPTY) {
            throw new IllegalStateException("예약 중인 좌석이 아닙니다.");
        }

        if(seat.getSeatStatus() == SeatStatus.USED) {
            throw new IllegalStateException("현재 입실 중인 좌석입니다.");
        }

        SeatReservation seatReservation = seatReservationRepository.findFirstBySeatIdAndIsDeleted(seatId, false)
                .orElseThrow(() -> new IllegalArgumentException("예약된 좌석을 찾을 수 없습니다."));

        if(!seatReservation.getUserId().equals(userId)) {
            throw new IllegalArgumentException("자신의 예약만 취소할 수 있습니다.");
        }

        // 현재 예약 내역 삭제
        seatReservation.delete();

        // 정상적으로 삭제되었으면
        // 현재 좌석 예약 / 입실 가능 상태로 변경
        if(seatReservation.getIsDeleted()) {
            seat.empty();
        }

        return SeatReservationResponse.from(seatReservation);
    }

    // 입실 시도 (Redis 분산 Lock 적용)
    @Transactional
    public SeatResponse tryEnterSeat(Long seatId, Long userId) {
        String lockKey = "seat: " + seatId;

        return distributedLock.executeWithLock(lockKey, 5, 10, () -> {
            return enterSeat(seatId, userId);
        });
    }

    // 입실
    @Transactional
    public SeatResponse enterSeat(Long seatId, Long userId) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new IllegalArgumentException("좌석을 찾을 수 없습니다."));

        // 나의 예약 찾기
        Optional<SeatReservation> reservation = seatReservationRepository
                .findByUserIdAndIsDeleted(userId, false);

        if(reservation.isPresent() && !reservation.get().getSeatId().equals(seatId)) {
            throw new IllegalStateException("내가 예약한 좌석이 아닙니다.");
        }

        // 현재 좌석의 예약 내역 확인
        Optional<SeatReservation> seatReservation = seatReservationRepository.findFirstBySeatIdAndIsDeleted(seatId, false);

        // 좌석이 예약 되어 있고
        // 예약자 id가 내 id가 다르면 => 다른 사람이 예약한 좌석
        if(seat.getSeatStatus() == SeatStatus.RESERVED && seatReservation.isPresent() && !seatReservation.get().getUserId().equals(userId)) {
            throw new IllegalStateException("이미 다른 사람이 예약한 좌석입니다.");
        }

        if(seat.getSeatStatus() == SeatStatus.USED) {
            throw new IllegalStateException("이미 입실 중인 좌석입니다.");
        }

        // 입실 중 상태로 변경
        seat.use();

        // 입실 내역 SeatReservation 생성
        if(seatReservation.isEmpty()) {
            SeatReservation enterReservation = SeatReservation.builder()
                    .seatId(seatId)
                    .userId(userId)
                    .build();

            seatReservationRepository.save(enterReservation);
        }


        return SeatResponse.from(seat);
    }

    // 퇴실
    @Transactional
    public SeatResponse exitSeat(Long seatId, Long userId) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new IllegalArgumentException("좌석을 찾을 수 없습니다."));


        SeatReservation seatReservation = seatReservationRepository.findFirstBySeatIdAndIsDeleted(seatId, false)
                .orElseThrow(() -> new IllegalArgumentException("입실된 좌석을 찾을 수 없습니다."));

        if (!seatReservation.getUserId().equals(userId)) {
            throw new IllegalStateException("자신의 좌석만 퇴실할 수 있습니다.");
        }

        if(seat.getSeatStatus() == SeatStatus.EMPTY) {
            throw new IllegalStateException("입실 중인 좌석이 아닙니다.");
        }

        if(seat.getSeatStatus() == SeatStatus.RESERVED) {
            throw new IllegalStateException("현재 예약 중인 좌석입니다.");
        }

        // 예약 내역 논리적 삭제
        seatReservation.delete();

        // 예약 & 입실 가능 상태로 변경
        seat.empty();

        return SeatResponse.from(seat);
    }

    // 이미 예약 or 입실중인지 확인
    private void validateUserNotAlreadyReservedOrUsing(Long userId) {
        boolean hasReservation = seatReservationRepository.existsByUserIdAndIsDeletedFalse(userId);
        if (hasReservation) {
            throw new IllegalStateException("이미 다른 좌석을 예약 or 입실한 상태입니다.");
        }
    }


    // 좌석 생성
    @Transactional
    public SeatResponse createSeat(String seatNumber) {
        if (seatRepository.findBySeatNumber(seatNumber).isPresent()) {
            throw new IllegalStateException("이미 존재하는 좌석입니다.");
        }

        Seat seat = Seat.builder()
                .seatNumber(seatNumber)
                .build();

        Seat saved = seatRepository.save(seat);
        return SeatResponse.from(saved);
    }
}
