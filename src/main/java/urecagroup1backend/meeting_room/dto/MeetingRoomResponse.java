package urecagroup1backend.meeting_room.dto;

import urecagroup1backend.meeting_room.domain.MeetingRoom;

/**
 * @file MeetingRoomResponse
 * @author 최인호
 * @description 미팅룸 응답 DTO
 */

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