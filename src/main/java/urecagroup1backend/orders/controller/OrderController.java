package urecagroup1backend.orders.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import urecagroup1backend.config.ApiResponse;
import urecagroup1backend.member.domain.CustomUserDetails;
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
@Slf4j// 로그 확인을 위해 추가
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController implements OrderControllerDocs {

    private final OrderService orderService;

    // [주문 진입 단계]
    @Override
    @PostMapping("/{snackId}")
    public ApiResponse<OrderResponse> enterOrder(@PathVariable Long snackId,
                                                 @AuthenticationPrincipal CustomUserDetails user) {
        // 유저 정보 유효성 검사 (방어 코드)
        if (user == null || user.getId() == null) {
            log.error("인증 실패: user 객체 또는 ID가 null입니다. user={}", user);
            throw new IllegalArgumentException("로그인 정보가 유효하지 않습니다. 토큰을 확인해주세요.");
        }
        OrderResponse response = orderService.enterOrder(snackId, user.getId());
        return new ApiResponse<>(HttpStatus.CREATED, "구매 진입 성공", response);
    }

    // [결제 단계] - 주문 상태를 결제 완료(PAID)로 변경
    @Override
    @PostMapping("/{orderId}/payment")
    public ApiResponse<OrderResponse> processPayment(@PathVariable Long orderId,
                                                     @AuthenticationPrincipal CustomUserDetails user) {
        OrderResponse response = orderService.Payment(orderId, user.getId());
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
    @PostMapping("/{orderId}/cancel")
    public ApiResponse<OrderResponse> cancelOrder(@PathVariable Long orderId,
                                                  @AuthenticationPrincipal CustomUserDetails user) {
        OrderResponse response = orderService.cancelOrder(orderId, user.getId());
        return new ApiResponse<>(HttpStatus.OK, "결제 취소 성공", response);
    }
}