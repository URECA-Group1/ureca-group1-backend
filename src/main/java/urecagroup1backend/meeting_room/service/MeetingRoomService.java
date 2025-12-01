package urecagroup1backend.meeting_room.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import urecagroup1backend.meeting_room.domain.MeetingRoom;
import urecagroup1backend.meeting_room.domain.MeetingRoomReservation;
import urecagroup1backend.meeting_room.dto.MeetingRoomResponse;
import urecagroup1backend.meeting_room.dto.ReservationResponse;
import urecagroup1backend.meeting_room.repository.MeetingRoomRepository;
import urecagroup1backend.meeting_room.repository.MeetingRoomReservationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeetingRoomService {

    private final MeetingRoomRepository meetingRoomRepository;
    private final MeetingRoomReservationRepository reservationRepository;

    public List<MeetingRoomResponse> getAvailableRooms() {
        return meetingRoomRepository.findByAvailable(true)
                .stream()
                .filter(room -> reservationRepository
                        .findActiveOrPendingReservationByMeetingRoomId(room.getId())
                        .isEmpty())
                .map(MeetingRoomResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReservationResponse enterReservationPage(Long meetingRoomId) {
        MeetingRoom meetingRoom = meetingRoomRepository.findById(meetingRoomId)
                .orElseThrow(() -> new IllegalArgumentException("회의실을 찾을 수 없습니다."));

        if (!meetingRoom.getAvailable()) {
            throw new IllegalStateException("사용 불가능한 회의실입니다.");
        }

        if (reservationRepository.findActiveOrPendingReservationByMeetingRoomId(meetingRoomId).isPresent()) {
            throw new IllegalStateException("이미 예약된 회의실입니다.");
        }

        MeetingRoomReservation reservation = MeetingRoomReservation.builder()
                .meetingRoom(meetingRoom)
                .status(MeetingRoomReservation.ReservationStatus.PENDING)
                .build();

        MeetingRoomReservation saved = reservationRepository.save(reservation);
        return ReservationResponse.from(saved);
    }

    @Transactional
    public ReservationResponse completeReservation(Long reservationId, String phoneNumber) {
        MeetingRoomReservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다."));

        if (reservation.getStatus() != MeetingRoomReservation.ReservationStatus.PENDING) {
            throw new IllegalStateException("예약 완료할 수 없는 상태입니다.");
        }

        reservation.completeReservation(phoneNumber);
        return ReservationResponse.from(reservation);
    }

    @Transactional
    public MeetingRoomResponse createMeetingRoom() {
        MeetingRoom meetingRoom = MeetingRoom.builder()
                .available(true)
                .build();

        MeetingRoom saved = meetingRoomRepository.save(meetingRoom);
        return MeetingRoomResponse.from(saved);
    }
}
