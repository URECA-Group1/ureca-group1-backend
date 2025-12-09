package urecagroup1backend.meeting_room.dto;

import urecagroup1backend.meeting_room.domain.MeetingRoomReservation;

/**
 * @file ReservationResponse
 * @author 최인호
 * @description 예약 응답 DTO
 */

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