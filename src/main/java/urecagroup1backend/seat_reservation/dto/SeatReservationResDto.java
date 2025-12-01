package urecagroup1backend.seat_reservation.dto;

import urecagroup1backend.seat_reservation.domain.SeatReservation;

import java.time.LocalDateTime;

public record SeatReservationResDto(
        Long id,
        Long seatId,
        LocalDateTime reservationTime
) {
    public static SeatReservationResDto from (SeatReservation seatReservation){
        return new SeatReservationResDto(
            seatReservation.getId(),
            seatReservation.getSeatId(),
            seatReservation.getCreatedAt()
        );
    }
}
