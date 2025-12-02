package urecagroup1backend.seat.dto;

import urecagroup1backend.seat.domain.SeatReservation;

import java.time.LocalDateTime;

// 좌석 예약 내역 응답 Dto
public record SeatReservationResponse(
        Long id,
        Long seatId,
        Boolean isDeleted
) {
    public static SeatReservationResponse from(SeatReservation seatReservation) {
        return new SeatReservationResponse(
                seatReservation.getId(),
                seatReservation.getSeatId(),
                seatReservation.getIsDeleted()
        );
    }
}
