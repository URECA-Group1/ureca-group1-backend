package urecagroup1backend.meeting_room.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import urecagroup1backend.meeting_room.domain.MeetingRoom;
import urecagroup1backend.meeting_room.domain.MeetingRoomReservation;
import urecagroup1backend.meeting_room.dto.MeetingRoomResponse;
import urecagroup1backend.meeting_room.dto.MeetingRoomStatusUpdate;
import urecagroup1backend.meeting_room.dto.ReservationResponse;
import urecagroup1backend.meeting_room.repository.MeetingRoomRepository;
import urecagroup1backend.meeting_room.repository.MeetingRoomReservationRepository;
import urecagroup1backend.member.domain.Member;
import urecagroup1backend.member.repository.MemberRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @file MeetingRoomService
 * @author 최인호
 * @description 미팅룸 서비스
 */

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeetingRoomService {

    private final MeetingRoomRepository meetingRoomRepository;
    private final MeetingRoomReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public static final String MEETING_ROOM_STATUS_TOPIC = "meeting-room-status";


    // Look-aside: 캐시 확인 → 없으면 DB 조회 후 캐싱
    @Cacheable(value = "availableRooms", key = "'all'")
    public List<MeetingRoomResponse> getAvailableRooms() {
        return meetingRoomRepository.findByAvailable(true)
                .stream()
                .filter(room -> reservationRepository
                        .findActiveOrPendingReservationByMeetingRoomId(room.getId())
                        .isEmpty())
                .map(MeetingRoomResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 회의실 예약 생성 (비즈니스 로직만 담당)
     * 분산락은 MeetingRoomFacadeService에서 처리
     */
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "availableRooms", key = "'all'"),
        @CacheEvict(value = "userReservations", key = "#email")
    })
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

        // Redis Pub/Sub: 회의실 상태 변경 메시지 발행
        redisTemplate.convertAndSend(MEETING_ROOM_STATUS_TOPIC, new MeetingRoomStatusUpdate(meetingRoomId, false));


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

    // 사용자별로 다른 데이터 → 키에 #email 사용
    @Cacheable(value = "userReservations", key = "#email")
    public List<ReservationResponse> getUserReservations(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        return reservationRepository.findActiveReservationsByMemberId(member.getId())
                .stream()
                .map(ReservationResponse::from)
                .collect(Collectors.toList());
    }

    // Evict는 쓰기 전략 설정해 놓은 것. 캐싱 값에 영향이 가는 로직이므로 사용.
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "availableRooms", key = "'all'"),
        @CacheEvict(value = "userReservations", key = "#email")
    })
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

        // Redis Pub/Sub: 회의실 상태 변경 메시지 발행
        redisTemplate.convertAndSend(MEETING_ROOM_STATUS_TOPIC, new MeetingRoomStatusUpdate(reservation.getMeetingRoom().getId(), true));
    }
}
