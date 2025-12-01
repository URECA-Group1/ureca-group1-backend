package urecagroup1backend.meeting_room.dto;

import urecagroup1backend.meeting_room.domain.MeetingRoom;

public record MeetingRoomResponse(
        Long id,
        Boolean available
) {
    public static MeetingRoomResponse from(MeetingRoom meetingRoom) {
        return new MeetingRoomResponse(
                meetingRoom.getId(),
                meetingRoom.getAvailable()
        );
    }
}