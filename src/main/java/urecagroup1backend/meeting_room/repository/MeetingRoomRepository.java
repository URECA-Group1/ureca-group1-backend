package urecagroup1backend.meeting_room.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import urecagroup1backend.meeting_room.domain.MeetingRoom;

import java.util.List;
import java.util.Optional;

/**
 * @file MeetingRoomRepository
 * @author 최인호
 * @description 미팅룸 레포지토리
 */

@Repository
public interface MeetingRoomRepository extends JpaRepository<MeetingRoom, Long> {

    List<MeetingRoom> findByAvailable(Boolean available);
}