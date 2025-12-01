package urecagroup1backend.meeting_room.dto;

import urecagroup1backend.meeting_room.domain.MeetingRoomReservation;

public record ReservationResponse(
        Long id,
        Long meetingRoomId,
        String phoneNumber,
        String status

) {
    public static ReservationResponse from(MeetingRoomReservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getMeetingRoom().getId(),
                reservation.getPhoneNumber(),
                reservation.getStatus().name()
        );
    }
}