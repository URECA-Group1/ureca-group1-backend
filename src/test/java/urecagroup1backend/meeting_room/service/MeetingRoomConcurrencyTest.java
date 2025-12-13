package urecagroup1backend.meeting_room.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import urecagroup1backend.common.lock.DistributedLock;
import urecagroup1backend.meeting_room.domain.MeetingRoom;
import urecagroup1backend.meeting_room.domain.MeetingRoomReservation;
import urecagroup1backend.meeting_room.repository.MeetingRoomRepository;
import urecagroup1backend.meeting_room.repository.MeetingRoomReservationRepository;
import urecagroup1backend.member.domain.Member;
import urecagroup1backend.member.repository.MemberRepository;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeetingRoomConcurrencyTest {

    private MeetingRoomService meetingRoomService;
    private MeetingRoomLockService meetingRoomFacadeService;
    private DistributedLock distributedLock;

    @Mock
    private MeetingRoomRepository meetingRoomRepository;
    @Mock
    private MeetingRoomReservationRepository reservationRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private RedissonClient redissonClient;
    @Mock
    private RLock rLock;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @BeforeEach
    void setUp() {
        // 의존성 수동 주입
        distributedLock = new DistributedLock(redissonClient);
        meetingRoomService = new MeetingRoomService(
                meetingRoomRepository,
                reservationRepository,
                memberRepository,
                redisTemplate
        );
        meetingRoomFacadeService = new MeetingRoomLockService(distributedLock, meetingRoomService);
    }

    @Test
    @DisplayName("동시에 여러 명이 같은 회의실 예약 시도 - 1명만 성공해야 함")
    void concurrentReservation_onlyOneSuccess() throws InterruptedException {
        // given
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        Long meetingRoomId = 1L;
        String userEmail = "test@example.com";

        Member testMember = Member.builder().email(userEmail).build();
        MeetingRoom testMeetingRoom = MeetingRoom.builder().id(meetingRoomId).available(true).build();

        // NPE 수정을 위해 status 추가
        MeetingRoomReservation testReservation = MeetingRoomReservation.builder()
                .id(1L)
                .meetingRoom(testMeetingRoom)
                .member(testMember)
                .status(MeetingRoomReservation.ReservationStatus.PENDING)
                .build();

        // Mocking
        when(redissonClient.getLock(anyString())).thenReturn(rLock);

        // Answer를 사용하여 스레드 안전하게 첫 호출에만 true를 반환
        final AtomicBoolean firstLock = new AtomicBoolean(true);
        when(rLock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenAnswer(invocation -> {
            return firstLock.getAndSet(false);
        });

        when(meetingRoomRepository.findById(meetingRoomId)).thenReturn(Optional.of(testMeetingRoom));
        when(memberRepository.findByEmail(userEmail)).thenReturn(Optional.of(testMember));
        when(reservationRepository.findActiveOrPendingReservationByMeetingRoomId(meetingRoomId)).thenReturn(
                Optional.empty());
        when(reservationRepository.save(any(MeetingRoomReservation.class))).thenReturn(testReservation);

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    meetingRoomFacadeService.enterReservationPage(meetingRoomId, userEmail);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(threadCount - 1);

        verify(reservationRepository, times(1)).save(any(MeetingRoomReservation.class));
    }
}