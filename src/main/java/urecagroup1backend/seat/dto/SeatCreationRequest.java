package urecagroup1backend.seat.dto;

// 좌석 생성 Dto

public record SeatCreationRequest(
        String seatNumber
) {
    public String toSeatNumber() {
        if (seatNumber == null
                || seatNumber.trim().isEmpty()
                || seatNumber.trim().length() > 4) {
            throw new IllegalArgumentException("seatNumber는 1글자 이상 4글자 이하여야 합니다.");
        }
        return seatNumber.trim();
    }
}
