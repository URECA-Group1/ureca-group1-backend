package urecagroup1backend.seat.api.dto;

import urecagroup1backend.seat.domain.SeatEntry;

import java.time.LocalDateTime;

public record SeatEntryResponse(
        Long id,
        Long seatId,
        LocalDateTime entryTime
) {
    public static SeatEntryResponse from(SeatEntry seatEntry) {
        return new SeatEntryResponse(
                seatEntry.getId(),
                seatEntry.getSeat().getId(),
                seatEntry.getEntryTime()
        );
    }
}
