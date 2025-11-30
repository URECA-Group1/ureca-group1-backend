package urecagroup1backend.orders.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import urecagroup1backend.config.ApiResponse;

import urecagroup1backend.orders.controller.docs.OrderControllerDocs;
import urecagroup1backend.orders.dto.OrderResponse;
import urecagroup1backend.orders.service.OrderService;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController implements OrderControllerDocs {

    private final OrderService orderService;

    @Override
    @PostMapping("/{snackId}")
    public ApiResponse<OrderResponse> enterOrder(@PathVariable Long snackId) {
        OrderResponse response = orderService.enterOrder(snackId);
        return new ApiResponse<>(HttpStatus.CREATED, "구매 진입 성공", response);
    }

    @Override
    @PostMapping("/{snackId}/cancel")
    public ApiResponse<Void> cancelOrder(@PathVariable Long snackId) {

        orderService.cancelOrder(snackId);

        return new ApiResponse<>(HttpStatus.OK, "주문 취소 및 재고 복구 완료");
    }
}