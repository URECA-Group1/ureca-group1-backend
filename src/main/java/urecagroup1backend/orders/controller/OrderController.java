package urecagroup1backend.orders.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import urecagroup1backend.config.ApiResponse;
import urecagroup1backend.member.domain.CustomUserDetails;
import urecagroup1backend.orders.Repository.OrderRepository;
import urecagroup1backend.orders.controller.docs.OrderControllerDocs;
import urecagroup1backend.orders.dto.OrderResponse;
import urecagroup1backend.orders.service.OrderProducer;
import urecagroup1backend.orders.service.OrderService;
import urecagroup1backend.snacks.repository.SnackRepository;

import java.util.List;

/*
@file OrderController.java
@author 박서연
@version 1.1
@since 2025-12-01
@description 이 파일은 주문, 결제, 내역 조회, 취소 기능을 수행하는 클래스입니다.
*/
@Slf4j// 로그 확인을 위해 추가
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController implements OrderControllerDocs {

    private final OrderService orderService;
    private final OrderProducer orderProducer;
    private final OrderRepository orderRepository;
    private final SnackRepository snackRepository;

    // [주문 진입 단계] - Kafka 적용
    @Override
    @PostMapping("/{snackId}")
    public ApiResponse<String> enterOrder(@PathVariable Long snackId,
                                                 @AuthenticationPrincipal CustomUserDetails user) {
        // 1. Kafka로 메시지 전송 (비동기)
        orderProducer.sendOrderRequest(snackId, user.getId());
        return new ApiResponse<>(HttpStatus.OK, "주문 요청이 접수되었습니다.", "SUCCESS");
    }

    // [결제 단계] - 주문 상태를 결제 완료(PAID)로 변경
    @Override
    @PostMapping("/{orderId}/payment")
    public ApiResponse<OrderResponse> processPayment(@PathVariable Long orderId,
                                                     @AuthenticationPrincipal CustomUserDetails user) {
        OrderResponse response = orderService.payment(orderId, user.getId());
        return new ApiResponse<>(HttpStatus.OK, "결제 성공", response);
    }

    // [주문 내역 조회] - 전체 혹은 특정 사용자의 주문 리스트 조회
    @Override
    @GetMapping("/list")
    public ApiResponse<List<OrderResponse>> getOrderHistory(@AuthenticationPrincipal CustomUserDetails user) {
        // User가 활성화되면 userId를 파라미터로 받거나 SecurityContext에서 가져와야 합니다.
        List<OrderResponse> responseList = orderService.getOrderHistory(user.getId());
        return new ApiResponse<>(HttpStatus.OK, "결제 내역 조회 성공", responseList);
    }

    // [결제 취소] - 주문 상태를 취소(CANCELED)로 변경
    @Override
    public ApiResponse<OrderResponse> cancelOrder(@PathVariable Long orderId,
                                                  @AuthenticationPrincipal CustomUserDetails user) {
        OrderResponse response = orderService.cancelOrder(orderId, user.getId());
        return new ApiResponse<>(HttpStatus.OK, "결제 취소 성공", response);
    }
}