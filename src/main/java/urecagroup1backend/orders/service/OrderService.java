package urecagroup1backend.orders.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import urecagroup1backend.member.domain.Member;
import urecagroup1backend.member.repository.MemberRepository;
import urecagroup1backend.orders.domain.Order;
import urecagroup1backend.orders.Repository.OrderRepository;
import urecagroup1backend.orders.dto.OrderResponse;
import urecagroup1backend.points.domain.Point;
import urecagroup1backend.points.repository.PointRepository;
import urecagroup1backend.snacks.domain.Snack;
import urecagroup1backend.snacks.repository.SnackRepository;

import java.util.List;
import java.util.stream.Collectors;

/*
@file OrderController.java
@author 박서연
@version 1.0
@since 2025-12-01
@description 이 파일은 주문기능을 수행하는 클래스입니다.
*/
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final SnackRepository snackRepository;
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final PointRepository pointRepository;

    @Transactional
    public void processOrder(Long snackId, Long userId) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        Snack snack = snackRepository.findById(snackId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 간식입니다."));

        Order.OrderStatus status;

        //  재고 차감 시도
        int updatedCount = snackRepository.purchaseSnack(snackId);

        if (updatedCount > 0) {
            // 성공: 재고 깎기 성공함
            status = Order.OrderStatus.SUCCESS;
            log.info("주문 성공! 결제 대기 상태로 저장. userId={}, snackId={}", userId, snackId);
        } else {
            // 실패: 재고가 없어서 못 깎음
            status = Order.OrderStatus.FAIL;
            log.info("주문 실패(재고부족). userId={}, snackId={}", userId, snackId);
        }

        // 주문 기록
        Order order = Order.builder()
                .snack(snack)
                .member(member)
                .status(status)
                .build();

        orderRepository.save(order);
    }

    //주문완료 -> 결제완료 (SUCCESS -> PAID)
    @Transactional
    public OrderResponse payment(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문이 없습니다."));

        if (!order.getMember().getId().equals(userId)) {
            throw new IllegalStateException("본인의 주문만 결제할 수 있습니다.");
        }

        if (order.getStatus() != Order.OrderStatus.SUCCESS) {
            throw new IllegalStateException("결제할 수 없는 상태의 주문입니다.");
        }

        // 상태 변경
        order.updateStatus(Order.OrderStatus.PAID);

        /// 포인트 차감
        // 1) 간식 가격 가져오기
        Long snackPrice = (long) order.getSnack().getPrice();

        // 2) 유저 포인트 조회
        Point point = pointRepository.findByMember_Id(userId)
                .orElseThrow(() -> new IllegalArgumentException("포인트 정보 없음"));

        // 3) 포인트 차감
        point.usePoint(snackPrice);

        return OrderResponse.from(order);

    }

    //주문 내역 조회
    @Transactional
    public List<OrderResponse> getOrderHistory(Long userId) {
        return orderRepository.findAllByMemberId(userId).stream()
                .map(OrderResponse::from)
                .collect(Collectors.toList());
    }

    // [결제 취소] 결제 완료(PAID)된 건은 환불 불가
    @Transactional
    public OrderResponse cancelOrder(Long orderId, Long userId) {
        // 주문 조회
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문이 없습니다."));

        // 본인 확인
        if (!order.getMember().getId().equals(userId)) {
            throw new IllegalStateException("본인의 주문만 취소할 수 있습니다.");
        }

        // 상태 검사 로직 강화
        // Case A: 이미 결제 완료된 경우 (환불 불가 정책 적용)
        if (order.getStatus() == Order.OrderStatus.PAID) {
            throw new IllegalStateException("이미 결제가 완료되어 취소(환불)할 수 없습니다.");
        }

        // Case B: 그 외 취소 불가능한 상태 (이미 취소됨, 재고 부족 실패 등)
        if (order.getStatus() != Order.OrderStatus.SUCCESS) {
            throw new IllegalStateException("취소할 수 없는 상태입니다.");
        }

        // 재고 복구 (결제 전이라도 재고는 잡아뒀었으니 다시 풀어줘야 함)
        snackRepository.updateStatus(order.getSnack().getId());

        // 주문 상태를 'CANCELED'로 변경
        order.updateStatus(Order.OrderStatus.CANCELED);

        return OrderResponse.from(order);
    }
}