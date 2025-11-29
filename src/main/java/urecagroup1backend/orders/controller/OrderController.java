package urecagroup1backend.orders.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import urecagroup1backend.config.ApiResponse;
import urecagroup1backend.orders.controller.docs.OrderControllerDocs;
import urecagroup1backend.orders.dto.OrderResponse;
import urecagroup1backend.orders.service.OrderService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/snacks/orders")
public class OrderController implements OrderControllerDocs {

    private final OrderService orderService;

    @Override
    @GetMapping
    public ApiResponse<List<OrderResponse>> getOrders() {
        List<OrderResponse> orders = orderService.getOrders();
        return new ApiResponse<>(HttpStatus.OK, "주문 내역 조회 성공", orders);
    }
}
