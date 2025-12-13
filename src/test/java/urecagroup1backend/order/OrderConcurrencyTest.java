package urecagroup1backend.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import urecagroup1backend.member.domain.Member;
import urecagroup1backend.member.domain.SocialType;
import urecagroup1backend.member.repository.MemberRepository;
import urecagroup1backend.orders.domain.Order;
import urecagroup1backend.orders.Repository.OrderRepository;
import urecagroup1backend.orders.service.OrderProducer;
import urecagroup1backend.snacks.domain.Snack;
import urecagroup1backend.snacks.repository.SnackRepository;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class OrderConcurrencyTest {

    @Autowired
    private OrderProducer orderProducer;
    @Autowired
    private SnackRepository snackRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private MemberRepository memberRepository;

    private Long targetSnackId;
    private Long soleMemberId; //유일한 회원의 ID

    @BeforeEach
    void setUp() {
        // 초기화
        orderRepository.deleteAll();
        snackRepository.deleteAll();
        memberRepository.deleteAll();

        // 간식 생성 (재고 10개)
        Snack snack = Snack.builder()
                .name("한정판 몽쉘")
                .price(1000)
                .quantity(10) // 재고 10개
                .build();
        targetSnackId = snackRepository.save(snack).getId();

        // 회원 생성 (딱 1명만 생성)
        // Member 엔티티의 socialType unique=true 제약 때문에 1명만 만들어야 함
        Member member = Member.builder()
                .name("광클유저")
                .email("user@test.com")
                .socialType(SocialType.KAKAO) // 여기서 KAKAO는 DB에 딱 한 번만 들어감
                .socialId("kakao_12345")
                .build();

        Member savedMember = memberRepository.save(member);
        soleMemberId = savedMember.getId();
    }

    @Test
    @DisplayName("동시성 테스트: 1명의 유저가 20번 동시에 요청 -> 10개만 성공하고 재고는 0이어야 함")
    void snack_order_concurrency_test() throws InterruptedException {
        // given
        int tryCount = 20; // 시도 횟수
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(tryCount);

        // when
        for (int i = 0; i < tryCount; i++) {
            executorService.submit(() -> {
                try {
                    orderProducer.sendOrderRequest(targetSnackId, soleMemberId);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 요청 전송 완료 대기

        // Kafka가 처리할 시간
        long startTime = System.currentTimeMillis();
        long maxWaitTime = 30000; // (PC 성능에 따라 조절)

        while (System.currentTimeMillis() - startTime < maxWaitTime) {
            List<Order> orders = orderRepository.findAll();
            // 100개가 다 쌓이면 즉시 통과! (30초 다 안 기다림)
            if (orders.size() == tryCount) {
                break;
            }
            Thread.sleep(100); // 0.1초마다 빼꼼 확인
        }

        // then (검증)

        // 재고 확인 (10개였는데 100번 요청왔으니 0개여야 함. 음수면 실패)
        Snack updatedSnack = snackRepository.findById(targetSnackId).orElseThrow();

        // 주문 내역 확인
        List<Order> allOrders = orderRepository.findAll();
        long successCount = allOrders.stream()
                .filter(o -> o.getStatus() == Order.OrderStatus.SUCCESS)
                .count();
        long failCount = allOrders.stream()
                .filter(o -> o.getStatus() == Order.OrderStatus.FAIL)
                .count();

        System.out.println("최종 재고: " + updatedSnack.getQuantity());
        System.out.println("총 주문 데이터: " + allOrders.size());
        System.out.println("성공(SUCCESS): " + successCount);
        System.out.println("실패(FAIL): " + failCount);

        // 검증
        assertThat(updatedSnack.getQuantity()).isEqualTo(0);
        assertThat(successCount).isEqualTo(10);
        assertThat(allOrders.size()).isEqualTo(20);
    }
}