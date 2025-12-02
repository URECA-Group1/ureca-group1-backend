package urecagroup1backend.orders.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import urecagroup1backend.config.ApiResponse;
import urecagroup1backend.orders.controller.docs.OrderControllerDocs;
import urecagroup1backend.orders.dto.OrderResponse;
import urecagroup1backend.orders.service.OrderService;

import java.util.List;

/*
@file OrderController.java
@author 박서연
@version 1.1
@since 2025-12-01
@description 이 파일은 주문, 결제, 내역 조회, 취소 기능을 수행하는 클래스입니다.
*/
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController implements OrderControllerDocs {

    private final OrderService orderService;

    // [주문 진입 단계] - 가주문 생성 (PENDING)
    @Override
    @PostMapping("/{snackId}")
    public ApiResponse<OrderResponse> enterOrder(@PathVariable Long snackId) {
        OrderResponse response = orderService.enterOrder(snackId);
        return new ApiResponse<>(HttpStatus.CREATED, "구매 진입 성공", response);
    }

    // [결제 단계] - 주문 상태를 결제 완료(PAID)로 변경
    @Override
    @PostMapping("/{orderId}/payment")
    public ApiResponse<OrderResponse> processPayment(@PathVariable Long orderId) {
        OrderResponse response = orderService.Payment(orderId);
        return new ApiResponse<>(HttpStatus.OK, "결제 성공", response);
    }

    // [결제 내역 조회] - 전체 혹은 특정 사용자의 주문 리스트 조회
    @Override
    @GetMapping("/list")
    public ApiResponse<List<OrderResponse>> getOrderHistory() {
        // 추후 User가 활성화되면 userId를 파라미터로 받거나 SecurityContext에서 가져와야 합니다.
        List<OrderResponse> responseList = orderService.getOrderHistory();
        return new ApiResponse<>(HttpStatus.OK, "결제 내역 조회 성공", responseList);
    }

    // [결제 취소] - 주문 상태를 취소(FAIL 혹은 별도 CANCELED)로 변경
    @Override
    @PostMapping("/{orderId}/cancel")
    public ApiResponse<OrderResponse> cancelOrder(@PathVariable Long orderId) {
        OrderResponse response = orderService.cancelOrder(orderId);
        return new ApiResponse<>(HttpStatus.OK, "결제 취소 성공", response);
    }
}