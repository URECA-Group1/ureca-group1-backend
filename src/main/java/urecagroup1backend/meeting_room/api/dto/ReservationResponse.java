package urecagroup1backend.meeting_room.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import urecagroup1backend.meeting_room.domain.MeetingRoomReservation;

import java.time.LocalDateTime;

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