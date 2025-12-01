package urecagroup1backend.seat.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import urecagroup1backend.seat.domain.SeatStatus;
import urecagroup1backend.seat.dto.SeatReservationResponse;
import urecagroup1backend.seat.dto.SeatResponse;
import urecagroup1backend.seat.domain.Seat;
import urecagroup1backend.seat.domain.SeatReservation;
import urecagroup1backend.seat.repository.SeatReservationRepository;
import urecagroup1backend.seat.repository.SeatRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeatService {
    private final SeatRepository seatRepository;
    private final SeatReservationRepository seatReservationRepository;

    // 전체 좌석 조회
    public List<SeatResponse> getAllSeats() {
        List<Seat> seats = seatRepository.findAll();

        List<SeatResponse> seatResponseList = new ArrayList<>();

        for(Seat s : seats) {
            seatResponseList.add(SeatResponse.from(s));
        }

        return seatResponseList;
    }

    // 나의 전체 예약 내역 조회

    // 나의 전체 입실 내역 조회

    // 예약
    public SeatReservationResponse reserveSeat(Long seatId, Long userId) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new IllegalArgumentException("좌석을 찾을 수 없습니다."));

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
    public SeatReservationResponse cancelReservation(Long seatId, Long userId) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new IllegalArgumentException("좌석을 찾을 수 없습니다."));

        if(seat.getSeatStatus() == SeatStatus.EMPTY) {
            throw new IllegalStateException("예약 중인 좌석이 아닙니다.");
        }

        if(seat.getSeatStatus() == SeatStatus.USED) {
            throw new IllegalStateException("현재 입실 중인 좌석입니다.");
        }

        SeatReservation seatReservation = seatReservationRepository.findFirstBySeatIdAndStatus(seatId, false)
                .orElseThrow(() -> new IllegalArgumentException("예약된 좌석을 찾을 수 없습니다."));

        // 현재 예약 내역 삭제
        seatReservation.delete();

        // 정상적으로 삭제되었으면
        // 현재 좌석 예약 / 입실 가능 상태로 변경
        if(seatReservation.getIsDeleted()) {
            seat.empty();
        }

        return SeatReservationResponse.from(seatReservation);
    }

    // 입실

    // 퇴실


    @Transactional
    public SeatReservationResponse entry(Long seatId, Long userId) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new IllegalArgumentException("좌석을 찾을 수 없습니다."));

        if (seatReservationRepository.findBySeatIdAndStatus(seatId, EntityStatus.ACTIVE).isPresent()) {
            throw new IllegalStateException("이미 입실된 좌석입니다.");
        }
        // todo: 좌석 예약 확인, 내가 한 예약인지 여부에 따라 진행(10분 내 입실 필요)

        SeatReservation seatReservation = SeatReservation.builder()
                .userId(userId)
                .seatId(seat.getId())
                .createdAt(LocalDateTime.now())
                .build();

        SeatReservation saved = seatReservationRepository.save(seatReservation);
        return SeatReservationResponse.from(saved);
    }

    @Transactional
    public SeatReservationResponse exitSeat(Long seatId, Long userId) {
        seatRepository.findById(seatId)
                .orElseThrow(() -> new IllegalArgumentException("좌석을 찾을 수 없습니다."));

        SeatReservation seatReservation = seatReservationRepository.findBySeatIdAndStatus(seatId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("입실된 좌석을 찾을 수 없습니다."));

        if (!seatReservation.getUserId().equals(userId)) {
           throw new IllegalStateException("자신의 좌석만 퇴실할 수 있습니다.");
        }

        seatReservation.delete();
        return SeatReservationResponse.from(seatReservation);
    }

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
