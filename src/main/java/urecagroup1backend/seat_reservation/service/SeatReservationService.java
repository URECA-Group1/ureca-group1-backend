package urecagroup1backend.seat_reservation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import urecagroup1backend.seat.domain.EntityStatus;
import urecagroup1backend.seat.domain.Seat;
import urecagroup1backend.seat.domain.SeatEntry;
import urecagroup1backend.seat.dto.SeatEntryResponse;
import urecagroup1backend.seat.repository.SeatEntryRepository;
import urecagroup1backend.seat.repository.SeatRepository;
import urecagroup1backend.seat_reservation.domain.SeatReservation;
import urecagroup1backend.seat_reservation.dto.SeatReservationResDto;
import urecagroup1backend.seat_reservation.repository.SeatReservationRepository;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class SeatReservationService {
    private final SeatReservationRepository seatReservationRepository; // 좌석 예약 정보
    private final SeatRepository seatRepository; // 좌석 정보
    private final SeatEntryRepository seatEntryRepository; // 입실 정보

    // 좌석 예약
    public SeatReservationResDto reserveSeat(Long seatId, Long userId) {

        // 좌석 존재 확인
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new IllegalArgumentException("해당 좌석을 찾을 수 없습니다."));

        // 이미 예약된 좌석인지 확인
        if (seatReservationRepository.findBySeatIdAndStatus(seatId, EntityStatus.ACTIVE).isPresent()) {
            throw new IllegalStateException("이미 예약된 좌석입니다.");
        }

        // 이미 입실된 좌석인지 확인
        if (seatEntryRepository.findBySeatIdAndStatus(seatId, EntityStatus.ACTIVE).isPresent()) {
            throw new IllegalStateException("이미 입실된 좌석입니다.");
        }

        // 내가 이미 예약한 적이 있는지 확인
        // question: 언제까지 예약 못하게 할 것인가?
        if(seatReservationRepository.findByUserId(userId).isPresent()) {
            throw new IllegalStateException("해당 유저는 이미 예약한 적이 있습니다.");
        }

        // todo: 내가 이미 입실한 적이 있는지 확인
        // question: 언제까지 예약 못하게 할 것인가?

        SeatReservation seatReservation = SeatReservation.builder()
                .seatId(seatId)
                .userId(userId)
                .build();

        SeatReservation saved = seatReservationRepository.save(seatReservation);
        return SeatReservationResDto.from(saved);
    }

    // 예약 취소
    // 예약 후 10분 뒤 입실이 없는 경우
    // 입실이 완료된 경우
    // => 같은 경우로 봐도 되는 걸까??
    @Transactional
    public SeatReservationResDto cancelReservedSeat(Long seatId, Long userId) {
        seatRepository.findById(seatId)
                .orElseThrow(() -> new IllegalArgumentException("좌석을 찾을 수 없습니다."));

        SeatReservation seatReservation = seatReservationRepository.findBySeatIdAndStatus(seatId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("예약된 좌석을 찾을 수 없습니다."));

        if (!seatReservation.getUserId().equals(userId)) {
            throw new IllegalStateException("자신의 좌석만 예약 취소할 수 있습니다.");
        }

        seatReservation.delete();
        return SeatReservationResDto.from(seatReservation);
    }

}
