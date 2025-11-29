package urecagroup1backend.orders.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import urecagroup1backend.orders.domain.SnackOrder;
import urecagroup1backend.orders.dto.OrderResponse;
import urecagroup1backend.orders.repository.SnackOrderRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final SnackOrderRepository snackOrderRepository;

    // 간식 주문 내역 조회 (지금은 모든 내역 조회)
    /// JWT 토큰에서 추출한 유저 ID로 조회 수정 필요
    public List<OrderResponse> getOrders() {
        List<SnackOrder> orders = snackOrderRepository.findAll();
        return orders.stream()
                .map(OrderResponse::from)
                .collect(Collectors.toList());
    }
}
