package urecagroup1backend.orders.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import urecagroup1backend.orders.Reposiotry.Order;
import urecagroup1backend.orders.Reposiotry.OrderRespository;
import urecagroup1backend.orders.dto.OrderResponse;
import urecagroup1backend.snacks.repository.Snack;
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
@Service
@RequiredArgsConstructor
public class OrderService {
    private final SnackRepository snackRepository;
    private final OrderRespository orderRepository;
//    private final UserRepository userRepository;

    @Transactional
    public OrderResponse enterOrder(Long snackId) {
        int result = snackRepository.purchaseSnack(snackId);

        if (result == 0) {
            throw new IllegalStateException("재고가 소진되었습니다.");
        }

        // 간식 엔티티 조회
        Snack snack = snackRepository.findById(snackId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 간식입니다."));
        //주문 생성
        Order order = Order.builder()
                .snack(snack)
                .status(Order.OrderStatus.PENDING)
                // .user(user)
                .build();

        Order savedOrder = orderRepository.save(order);

        return OrderResponse.from(savedOrder);
    }
    //주문 상태 변경 (PENDING -> PAID)
    @Transactional
    public OrderResponse Payment(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문을 찾을 수 없습니다."));

        // 유효성 검사: 대기 상태가 아니면 결제 불가
        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new IllegalStateException("결제할 수 없는 상태의 주문입니다.");
        }

        // 상태 변경 (Dirty Checking)
        order.updateStatus(Order.OrderStatus.PAID);

        return OrderResponse.from(order);
    }
    //주문 내역 조회
    @Transactional
    public List<OrderResponse> getOrderHistory() {
        return orderRepository.findAll().stream()
                .map(OrderResponse::from)
                .collect(Collectors.toList());
    }

    //결제 취소
    @Transactional
    public OrderResponse cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문을 찾을 수 없습니다."));

        // 이미 취소된 주문인지 확인
        if (order.getStatus() == Order.OrderStatus.CANCELED || order.getStatus() == Order.OrderStatus.FAIL) {
            throw new IllegalStateException("이미 취소된 주문입니다.");
        }

        // 1. 재고 복구 (SnackStatus: 0 -> 1)
        snackRepository.updateStatus(order.getSnack().getId());

        // 2. 주문 상태 취소로 변경
        order.updateStatus(Order.OrderStatus.CANCELED);

        return OrderResponse.from(order);
    }
}