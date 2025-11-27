package urecagroup1backend.seat.api.dto;

import urecagroup1backend.seat.domain.Seat;

public record SeatResponse(
        Long id,
        String seatNumber
) {
    public static SeatResponse from(Seat seat) {
        return new SeatResponse(
                seat.getId(),
                seat.getSeatNumber()
        );
    }
}
