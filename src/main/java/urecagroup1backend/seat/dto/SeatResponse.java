package urecagroup1backend.seat.dto;

import urecagroup1backend.seat.domain.Seat;
import urecagroup1backend.seat.domain.SeatStatus;

// 좌석 정보 응답 Dto
public record SeatResponse(
        Long id,
        String seatNumber,
        SeatStatus seatStatus
) {
    public static SeatResponse from(Seat seat) {
        return new SeatResponse(
                seat.getId(),
                seat.getSeatNumber(),
                seat.getSeatStatus()
        );
    }
}
