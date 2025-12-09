package urecagroup1backend.meeting_room.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import urecagroup1backend.common.lock.DistributedLock;
import urecagroup1backend.meeting_room.domain.MeetingRoom;
import urecagroup1backend.meeting_room.domain.MeetingRoomReservation;
import urecagroup1backend.meeting_room.dto.MeetingRoomResponse;
import urecagroup1backend.meeting_room.dto.ReservationResponse;
import urecagroup1backend.meeting_room.repository.MeetingRoomRepository;
import urecagroup1backend.meeting_room.repository.MeetingRoomReservationRepository;
import urecagroup1backend.member.domain.Member;
import urecagroup1backend.member.repository.MemberRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeetingRoomService {

    private final MeetingRoomRepository meetingRoomRepository;
    private final MeetingRoomReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final DistributedLock distributedLock;

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
    public ReservationResponse enterReservationPage(Long meetingRoomId, String email) {
        String lockKey = "meeting-room:reservation:" + meetingRoomId;

        return distributedLock.executeWithLock(lockKey, 5, 10, () -> {
            return createReservation(meetingRoomId, email);
        });
    }

    @Transactional
    public ReservationResponse createReservation(Long meetingRoomId, String email) {
        MeetingRoom meetingRoom = meetingRoomRepository.findById(meetingRoomId)
                .orElseThrow(() -> new IllegalArgumentException("회의실을 찾을 수 없습니다."));

        if (!meetingRoom.getAvailable()) {
            throw new IllegalStateException("사용 불가능한 회의실입니다.");
        }

        if (reservationRepository.findActiveOrPendingReservationByMeetingRoomId(meetingRoomId).isPresent()) {
            throw new IllegalStateException("이미 예약된 회의실입니다.");
        }

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        MeetingRoomReservation reservation = MeetingRoomReservation.builder()
                .meetingRoom(meetingRoom)
                .member(member)
                .status(MeetingRoomReservation.ReservationStatus.PENDING)
                .build();

        MeetingRoomReservation saved = reservationRepository.save(reservation);
        return ReservationResponse.from(saved);
    }

    @Transactional
    public ReservationResponse completeReservation(Long reservationId, String email, String phoneNumber) {
        MeetingRoomReservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다."));

        if (reservation.getStatus() != MeetingRoomReservation.ReservationStatus.PENDING) {
            throw new IllegalStateException("예약 완료할 수 없는 상태입니다.");
        }

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        reservation.completeReservation(member, phoneNumber);
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

    public List<ReservationResponse> getUserReservations(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        return reservationRepository.findActiveReservationsByMemberId(member.getId())
                .stream()
                .map(ReservationResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void cancelReservation(Long reservationId, String email) {
        MeetingRoomReservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다."));

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        if (!reservation.getMember().getId().equals(member.getId())) {
            throw new IllegalStateException("본인의 예약만 취소할 수 있습니다.");
        }

        if (reservation.getStatus() == MeetingRoomReservation.ReservationStatus.CANCELLED) {
            throw new IllegalStateException("이미 취소된 예약입니다.");
        }

        reservation.cancel();
    }
}
