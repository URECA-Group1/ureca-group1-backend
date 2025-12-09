package urecagroup1backend.meeting_room.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @file MeetingRoomStatusUpdate
 * @author 최인호
 * @description pub/sub DTO
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MeetingRoomStatusUpdate {
    private Long meetingRoomId;
    private boolean isAvailable;
}
