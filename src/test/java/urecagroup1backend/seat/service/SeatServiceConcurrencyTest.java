package urecagroup1backend.seat.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import urecagroup1backend.seat.domain.Seat;
import urecagroup1backend.seat.domain.SeatStatus;
import urecagroup1backend.seat.dto.SeatReservationResponse;
import urecagroup1backend.seat.dto.SeatResponse;
import urecagroup1backend.seat.repository.SeatRepository;
import urecagroup1backend.seat.repository.SeatReservationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class SeatServiceConcurrencyTest {
    @Autowired
    private SeatFacadeService seatService;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private SeatReservationRepository seatReservationRepository;

    private Seat seat;

    @BeforeEach
    void setUp() {
        // 매 테스트마다 DB 초기화
        seatReservationRepository.deleteAll();
        seatRepository.deleteAll();

        seat = Seat.builder()
                .seatNumber("T001")
                .seatStatus(SeatStatus.EMPTY)
                .build();

        seat = seatRepository.save(seat);
    }

    @AfterEach
    void clear() {
        // 매 테스트 종료 후 DB 초기화
        seatReservationRepository.deleteAll();
        seatRepository.deleteAll();
    }

    @Test
    @DisplayName("상황1 - 한 좌석에 대해 여러 예약 시도 시 1명만 성공해야 한다")
    void concurrentReserveSameSeatMultipleUsers() throws Exception {
        int threadCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        // CountDownLatch: 여러 쓰레드를 특정 시점까지 대기시킨 뒤,
        // 조건이 만족되면 한 번에(or 일부만) 달려가도록 만들어주는 동기화 도구
        // 숫자가 0이 될 때까지 쓰레드들을 기다리게 하고,
        // 0이 되는 순간 대기하던 모든 쓰레드를 동시에 실행시키는 장치
        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            final long userId = i + 1L; // 각기 다른 유저 ID

            futures.add(executor.submit(() -> {
                try {
                    ready.countDown();
                    start.await(); // 동시에 시작

                    SeatReservationResponse response =
                            seatService.tryReserveSeat(seat.getId(), userId);

                    successCount.incrementAndGet();
                } catch (Exception e) {
                    // IllegalStateException("이미 예약된 좌석입니다.") 등
                    failCount.incrementAndGet();
                }
            }));
        }

        // 모든 스레드 준비 완료 후 일제히 시작
        ready.await();
        start.countDown();

        // 모든 작업 완료 대기
        for (Future<?> f : futures) {
            f.get();
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        System.out.println("successCount = " + successCount.get());
        System.out.println("failCount = " + failCount.get());
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(threadCount - 1);

        Seat finalSeat = seatRepository.findById(seat.getId()).orElseThrow();
        assertThat(finalSeat.getSeatStatus()).isEqualTo(SeatStatus.RESERVED);

        long reservationCount = seatReservationRepository
                .findAll()
                .stream()
                .filter(r -> !r.getIsDeleted())
                .count();
        assertThat(reservationCount).isEqualTo(1);
    }

    @Test
    @DisplayName("상황2 - 한 좌석에 대해 예약과 입실을 동시에 시도하면 둘 중 하나만 성공한다")
    void concurrentReserveAndEnterSameSeat() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);

        AtomicInteger reserveSuccess = new AtomicInteger();
        AtomicInteger reserveFail = new AtomicInteger();
        AtomicInteger enterSuccess = new AtomicInteger();
        AtomicInteger enterFail = new AtomicInteger();

        List<Future<?>> futures = new ArrayList<>();

        // 예약 스레드 (userId = 1)
        futures.add(executor.submit(() -> {
            try {
                ready.countDown();
                start.await();

                seatService.tryReserveSeat(seat.getId(), 1L);
                reserveSuccess.incrementAndGet();
                System.out.println("예약 성공 userId = " + 1L);
            } catch (Exception e) {
                reserveFail.incrementAndGet();
            }
        }));

        // 입실 스레드 (userId = 2)
        futures.add(executor.submit(() -> {
            try {
                ready.countDown();
                start.await();

                SeatResponse response = seatService.tryEnterSeat(seat.getId(), 2L);
                enterSuccess.incrementAndGet();
                System.out.println("입실 성공 userId = " + 2L);
            } catch (Exception e) {
                enterFail.incrementAndGet();
            }
        }));

        ready.await();
        start.countDown();

        for (Future<?> f : futures) {
            f.get();
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        System.out.println("enterSuccess.get() = " + enterSuccess.get());
        System.out.println("enterFail.get() = " + enterFail.get());
        System.out.println("reserveSuccess.get() = " + reserveSuccess.get());
        System.out.println("reserveFail.get() = " + reserveFail.get());

        // 둘 다 성공하는 일은 없어야 한다
        assertThat(reserveSuccess.get() + enterSuccess.get())
                .as("예약 성공 + 입실 성공 합은 1이어야 한다")
                .isEqualTo(1);

        // 둘 중 하나는 반드시 실패해야 함
        assertThat(reserveFail.get() + enterFail.get())
                .as("예약 실패 + 입실 실패 합은 1이어야 한다")
                .isEqualTo(1);

        Seat finalSeat = seatRepository.findById(seat.getId()).orElseThrow();
        assertThat(finalSeat.getSeatStatus())
                .as("최종 좌석 상태는 RESERVED 또는 USED 여야 한다")
                .isIn(SeatStatus.RESERVED, SeatStatus.USED);
    }

    @Test
    @DisplayName("상황3 - 한 좌석에 대해 여러 입실 시도 시 1명만 성공해야 한다")
    void concurrentEnterSameSeatMultipleUsers() throws Exception {
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            final long userId = i + 1L;

            futures.add(executor.submit(() -> {
                try {
                    ready.countDown();
                    start.await();

                    seatService.tryEnterSeat(seat.getId(), userId);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                }
            }));
        }

        ready.await();
        start.countDown();

        for (Future<?> f : futures) {
            f.get();
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(threadCount - 1);

        Seat finalSeat = seatRepository.findById(seat.getId()).orElseThrow();
        assertThat(finalSeat.getSeatStatus()).isEqualTo(SeatStatus.USED);

        long activeReservations = seatReservationRepository.findAll().stream()
                .filter(r -> !r.getIsDeleted())
                .count();

        // 구현에 따라 1개 또는 그 이상(중복 생성 후 delete)일 수 있으므로
        assertThat(activeReservations).isEqualTo(1);
    }
}


