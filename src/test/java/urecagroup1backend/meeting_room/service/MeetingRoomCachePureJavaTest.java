package urecagroup1backend.meeting_room.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import urecagroup1backend.common.lock.DistributedLock;
import urecagroup1backend.common.lock.DistributedLockExecutor;
import urecagroup1backend.meeting_room.domain.MeetingRoom;
import urecagroup1backend.meeting_room.domain.MeetingRoomReservation;
import urecagroup1backend.meeting_room.dto.MeetingRoomResponse;
import urecagroup1backend.meeting_room.dto.ReservationResponse;
import urecagroup1backend.meeting_room.repository.MeetingRoomRepository;
import urecagroup1backend.meeting_room.repository.MeetingRoomReservationRepository;
import urecagroup1backend.member.domain.Member;
import urecagroup1backend.member.domain.SocialType;
import urecagroup1backend.member.repository.MemberRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mockingDetails;

@SpringJUnitConfig
@Import(MeetingRoomCachePureJavaTest.CacheTestConfig.class)
class MeetingRoomCachePureJavaTest {

    @TestConfiguration
    @EnableCaching  // 캐싱 활성화
    static class CacheTestConfig {

        @Bean
        public CacheManager cacheManager() {
            // Redis 대신 메모리 기반 ConcurrentMap 사용
            // 실제 Redis와 동일하게 @Cacheable/@CacheEvict 동작
            return new ConcurrentMapCacheManager(
                    "availableRooms",
                    "userReservations"
            );
        }

        @Bean
        public MeetingRoomService meetingRoomService(
                MeetingRoomRepository meetingRoomRepository,
                MeetingRoomReservationRepository reservationRepository,
                MemberRepository memberRepository,
                RedisTemplate<String, Object> redisTemplate,
                DistributedLockExecutor lockExecutor) {
            return new MeetingRoomService(
                    meetingRoomRepository,
                    reservationRepository,
                    memberRepository,
                    redisTemplate,
                    lockExecutor
            );
        }

        @Bean
        public MeetingRoomRepository meetingRoomRepository() {
            return mock(MeetingRoomRepository.class);
        }

        @Bean
        public MeetingRoomReservationRepository reservationRepository() {
            return mock(MeetingRoomReservationRepository.class);
        }

        @Bean
        public MemberRepository memberRepository() {
            return mock(MemberRepository.class);
        }

        @Bean
        public DistributedLock distributedLock() {
            DistributedLock lock = mock(DistributedLock.class);
            when(lock.executeWithLock(anyString(), anyLong(), anyLong(), any()))
                    .thenAnswer(invocation -> {
                        Object supplier = invocation.getArgument(3);
                        if (supplier instanceof java.util.function.Supplier) {
                            return ((java.util.function.Supplier<?>) supplier).get();
                        }
                        return null;
                    });
            return lock;
        }

        @Bean
        public DistributedLockExecutor distributedLockExecutor(DistributedLock distributedLock) {
            return new DistributedLockExecutor(distributedLock);
        }

        @Bean
        public RedisTemplate<String, Object> redisTemplate() {
            return mock(RedisTemplate.class); // RedisTemplate Mock Bean 추가
        }
    }

    @Autowired
    private MeetingRoomService service;

    @Autowired
    private MeetingRoomRepository meetingRoomRepository;

    @Autowired
    private MeetingRoomReservationRepository reservationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CacheManager cacheManager;

    private MeetingRoom room1;
    private MeetingRoom room2;
    private Member member;

    @BeforeEach
    void setUp() {
        // 각 테스트 전에 캐시 초기화
        cacheManager.getCacheNames()
                .forEach(name -> cacheManager.getCache(name).clear());

        // Mock 초기화
        reset(meetingRoomRepository, reservationRepository, memberRepository);

        // 테스트 데이터 준비
        room1 = MeetingRoom.builder()
                .id(1L)
                .available(true)
                .build();

        room2 = MeetingRoom.builder()
                .id(2L)
                .available(true)
                .build();

        member = Member.builder()
                .id(1L)
                .email("test@test.com")
                .name("테스터")
                .socialType(SocialType.GOOGLE)
                .build();
    }

    @Test
    @DisplayName("캐시 적중 테스트. 첫 조회는 DB, 두 번째는 캐시")
    void getAvailableRooms_CacheHit() {
        when(meetingRoomRepository.findByAvailable(true))
                .thenReturn(Arrays.asList(room1, room2));
        when(reservationRepository.findActiveOrPendingReservationByMeetingRoomId(anyLong()))
                .thenReturn(Optional.empty());

        // when: 첫 번째 조회 (Cache Miss)
        List<MeetingRoomResponse> firstResult = service.getAvailableRooms();

        // then: DB 조회 1번 발생
        verify(meetingRoomRepository, times(1)).findByAvailable(true);
        assertThat(firstResult).hasSize(2);

        // when: 두 번째 조회 (Cache Hit)
        List<MeetingRoomResponse> secondResult = service.getAvailableRooms();

        // then: DB 추가 조회 없음 (여전히 1번만) → 캐시 사용
        verify(meetingRoomRepository, times(1)).findByAvailable(true);
        assertThat(secondResult).hasSize(2);

        // 캐시에 데이터가 있는지 직접 확인
        Cache cache = cacheManager.getCache("availableRooms");
        assertThat(cache).isNotNull();
        assertThat(cache.get("all")).isNotNull();
        assertThat(cache.get("all").get()).isInstanceOf(List.class);
    }

    @Test
    @DisplayName("캐시 키 테스트. 다른 email은 다른 캐시 사용")
    void getUserReservations_DifferentCacheKeys() {
        // given: 두 명의 회원
        Member member1 = Member.builder()
                .id(1L)
                .email("user1@test.com")
                .name("유저1")
                .socialType(SocialType.GOOGLE)
                .build();

        Member member2 = Member.builder()
                .id(2L)
                .email("user2@test.com")
                .name("유저2")
                .socialType(SocialType.KAKAO)
                .build();

        MeetingRoomReservation reservation1 = MeetingRoomReservation.builder()
                .id(1L)
                .meetingRoom(room1)
                .member(member1)
                .status(MeetingRoomReservation.ReservationStatus.ACTIVE)
                .build();

        MeetingRoomReservation reservation2 = MeetingRoomReservation.builder()
                .id(2L)
                .meetingRoom(room2)
                .member(member2)
                .status(MeetingRoomReservation.ReservationStatus.ACTIVE)
                .build();

        when(memberRepository.findByEmail("user1@test.com")).thenReturn(Optional.of(member1));
        when(memberRepository.findByEmail("user2@test.com")).thenReturn(Optional.of(member2));
        when(reservationRepository.findActiveReservationsByMemberId(1L))
                .thenReturn(Arrays.asList(reservation1));
        when(reservationRepository.findActiveReservationsByMemberId(2L))
                .thenReturn(Arrays.asList(reservation2));

        // when: user1 조회 (Cache Miss)
        List<ReservationResponse> user1First = service.getUserReservations("user1@test.com");
        // user1 재조회 (Cache Hit)
        List<ReservationResponse> user1Second = service.getUserReservations("user1@test.com");

        // then: user1의 DB 조회는 1번만 (캐시 사용)
        verify(reservationRepository, times(1)).findActiveReservationsByMemberId(1L);
        assertThat(user1First).hasSize(1);
        assertThat(user1Second).hasSize(1);

        // when: user2 조회 (다른 키라서 Cache Miss)
        List<ReservationResponse> user2Result = service.getUserReservations("user2@test.com");

        // then: user2는 별도로 DB 조회 발생 (다른 캐시 키)
        verify(reservationRepository, times(1)).findActiveReservationsByMemberId(2L);
        assertThat(user2Result).hasSize(1);

        // 캐시에 두 키가 모두 저장되어 있는지 확인
        Cache cache = cacheManager.getCache("userReservations");
        assertThat(cache).isNotNull();
        assertThat(cache.get("user1@test.com")).isNotNull();
        assertThat(cache.get("user2@test.com")).isNotNull();
    }

    @Test
    @DisplayName("@CacheEvict 테스트: 캐시 삭제 확인")
    void enterReservationPage_CacheEvict() {
        when(meetingRoomRepository.findByAvailable(true))
                .thenReturn(Arrays.asList(room1, room2));
        when(reservationRepository.findActiveOrPendingReservationByMeetingRoomId(anyLong()))
                .thenReturn(Optional.empty());

        service.getAvailableRooms();

        Cache availableRoomsCache = cacheManager.getCache("availableRooms");
        assertThat(availableRoomsCache.get("all")).isNotNull();

        when(meetingRoomRepository.findById(1L)).thenReturn(Optional.of(room1));
        when(memberRepository.findByEmail("test@test.com")).thenReturn(Optional.of(member));
        when(reservationRepository.save(any())).thenAnswer(invocation -> {
            MeetingRoomReservation reservation = invocation.getArgument(0);
            return MeetingRoomReservation.builder()
                    .id(1L)
                    .meetingRoom(reservation.getMeetingRoom())
                    .member(reservation.getMember())
                    .status(reservation.getStatus())
                    .build();
        });

        // when: @CacheEvict가 붙은 메서드 호출 (createReservation에 @CacheEvict 있음)
        service.createReservation(1L, "test@test.com");

        // then: availableRooms 캐시 삭제됨
        assertThat(availableRoomsCache.get("all")).isNull();

        // then: userReservations 캐시도 삭제됨 (email 키)
        Cache userReservationsCache = cacheManager.getCache("userReservations");
        assertThat(userReservationsCache.get("test@test.com")).isNull();

        // when: 다시 조회하면 DB 조회 발생
        reset(meetingRoomRepository);
        when(meetingRoomRepository.findByAvailable(true))
                .thenReturn(Arrays.asList(room1, room2));

        service.getAvailableRooms();

        // then: DB 재조회 발생 (캐시가 삭제되었으므로)
        verify(meetingRoomRepository, times(1)).findByAvailable(true);
    }

    @Test
    @DisplayName("cancelReservation() @CacheEvict 테스트: 캐시 삭제 확인")
    void cancelReservation_CacheEvict() {
        // given: 캐시 준비
        when(memberRepository.findByEmail("test@test.com")).thenReturn(Optional.of(member));
        when(reservationRepository.findActiveReservationsByMemberId(1L))
                .thenReturn(Arrays.asList());

        service.getUserReservations("test@test.com");  // 캐시에 저장

        Cache userReservationsCache = cacheManager.getCache("userReservations");
        assertThat(userReservationsCache.get("test@test.com")).isNotNull();

        // given: cancelReservation을 위한 Mock 설정
        MeetingRoomReservation reservation = MeetingRoomReservation.builder()
                .id(1L)
                .meetingRoom(room1)
                .member(member)
                .status(MeetingRoomReservation.ReservationStatus.ACTIVE)
                .build();

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(memberRepository.findByEmail("test@test.com")).thenReturn(Optional.of(member));

        // when: @CacheEvict가 붙은 취소 메서드 호출
        service.cancelReservation(1L, "test@test.com");

        // then: availableRooms, userReservations 캐시 모두 삭제됨
        Cache availableRoomsCache = cacheManager.getCache("availableRooms");
        assertThat(availableRoomsCache.get("all")).isNull();
        assertThat(userReservationsCache.get("test@test.com")).isNull();
    }

    @Test
    @DisplayName("캐시 TTL 시뮬레이션: 캐시 수동 삭제 후 재조회")
    void cacheExpiration_Simulation() {
        // given: 캐시 저장
        when(meetingRoomRepository.findByAvailable(true))
                .thenReturn(Arrays.asList(room1, room2));
        when(reservationRepository.findActiveOrPendingReservationByMeetingRoomId(anyLong()))
                .thenReturn(Optional.empty());

        service.getAvailableRooms();
        verify(meetingRoomRepository, times(1)).findByAvailable(true);

        // when: 캐시 만료 시뮬레이션 (수동 삭제)
        Cache cache = cacheManager.getCache("availableRooms");
        cache.evict("all");

        // then: 캐시가 없으므로 다시 조회 시 DB 접근
        service.getAvailableRooms();
        verify(meetingRoomRepository, times(2)).findByAvailable(true);
    }

    @Test
    @DisplayName("동시성 테스트: 캐시가 있을 때 여러 스레드가 캐시 공유")
    void concurrentCacheAccess() throws InterruptedException {
        // given: 먼저 캐시를 채워둠
        when(meetingRoomRepository.findByAvailable(true))
                .thenReturn(Arrays.asList(room1, room2));
        when(reservationRepository.findActiveOrPendingReservationByMeetingRoomId(anyLong()))
                .thenReturn(Optional.empty());

        // 첫 호출로 캐시 준비
        service.getAvailableRooms();
        verify(meetingRoomRepository, times(1)).findByAvailable(true);

        // Mock 호출 기록 초기화
        reset(meetingRoomRepository);

        // when: 캐시가 있는 상태에서 10개의 스레드가 동시에 접근
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(() -> {
                List<MeetingRoomResponse> result = service.getAvailableRooms();
                assertThat(result).hasSize(2);
            });
            threads[i].start();
        }

        // 모든 스레드 종료 대기
        for (Thread thread : threads) {
            thread.join();
        }

        // then: 캐시가 있으므로 DB 조회 발생 안 함
        verify(meetingRoomRepository, never()).findByAvailable(true);
    }

    @Test
    @DisplayName("Cache Stampede 현상: 캐시 미스 시 여러 스레드가 동시 DB 조회")
    void cacheStampede() throws InterruptedException {
        when(meetingRoomRepository.findByAvailable(true))
                .thenReturn(Arrays.asList(room1, room2));
        when(reservationRepository.findActiveOrPendingReservationByMeetingRoomId(anyLong()))
                .thenReturn(Optional.empty());

        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(() -> {
                service.getAvailableRooms();
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        int actualInvocations = mockingDetails(meetingRoomRepository)
                .getInvocations()
                .size();

        assertThat(actualInvocations).isGreaterThan(0);
    }
}