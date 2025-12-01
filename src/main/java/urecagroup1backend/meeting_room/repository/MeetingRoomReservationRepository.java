package urecagroup1backend.meeting_room.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import urecagroup1backend.meeting_room.domain.MeetingRoomReservation;

import java.util.List;
import java.util.Optional;

@Repository
public interface MeetingRoomReservationRepository extends JpaRepository<MeetingRoomReservation, Long> {

    @Query("SELECT r FROM MeetingRoomReservation r WHERE r.meetingRoom.id = :meetingRoomId " +
            "AND r.status IN ('PENDING', 'ACTIVE')")
    Optional<MeetingRoomReservation> findActiveOrPendingReservationByMeetingRoomId(@Param("meetingRoomId") Long meetingRoomId);

    List<MeetingRoomReservation> findByMeetingRoomIdAndStatus(Long meetingRoomId, MeetingRoomReservation.ReservationStatus status);
}