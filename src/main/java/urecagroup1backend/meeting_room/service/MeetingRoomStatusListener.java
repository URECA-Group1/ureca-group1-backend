package urecagroup1backend.meeting_room.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;
import urecagroup1backend.meeting_room.dto.MeetingRoomStatusUpdate;

import java.io.IOException;

/**
 * @file MeetingRoomStatusListener
 * @author 최인호
 * @description 메시지 리스너로, Redis Pub/Sub를 통해 회의실 상태 업데이트 메시지를 수신합니다. (전송로직 미구현)
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class MeetingRoomStatusListener implements MessageListener {

    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            MeetingRoomStatusUpdate statusUpdate = objectMapper.readValue(message.getBody(), MeetingRoomStatusUpdate.class);
            log.info("Received meeting room status update: Room ID = {}, isAvailable = {}",
                    statusUpdate.getMeetingRoomId(), statusUpdate.isAvailable());

            // TODO: WebSocket 등을 통해 클라이언트에게 실시간 업데이트 전송 로직 추가하면 브로드캐스팅 가능함.
        } catch (IOException e) {
            log.error("Failed to parse meeting room status update message", e);
        }
    }
}
