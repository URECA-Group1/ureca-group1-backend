package urecagroup1backend.meeting_room.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import urecagroup1backend.common.lock.DistributedLock;
import urecagroup1backend.meeting_room.dto.ReservationResponse;

/**
 * @file MeetingRoomFacadeService
 * @author 최인호
 * @description 미팅룸 Facade 서비스 - 분산락 처리
 *
 * Facade 패턴:
 * - 이 클래스는 분산락 획득/해제만 담당
 * - 실제 비즈니스 로직은 MeetingRoomService에 위임
 * - Lock { Transaction { 비즈니스 로직 } } 순서 보장
 */
@Service
@RequiredArgsConstructor
public class MeetingRoomFacadeService {

    private final DistributedLock distributedLock;
    private final MeetingRoomService meetingRoomService;

    public ReservationResponse enterReservationPage(Long meetingRoomId, String email) {
        String lockKey = "meeting-room:reservation:" + meetingRoomId;

        return distributedLock.executeWithLock(lockKey, 5, 10, () -> {
            return meetingRoomService.createReservation(meetingRoomId, email);
        });
    }
}
