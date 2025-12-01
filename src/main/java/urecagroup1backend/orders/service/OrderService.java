package urecagroup1backend.orders.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import urecagroup1backend.orders.Reposiotry.Order;
import urecagroup1backend.orders.Reposiotry.OrderRespository;
import urecagroup1backend.orders.dto.OrderResponse;
import urecagroup1backend.snacks.repository.Snack;
import urecagroup1backend.snacks.repository.SnackRepository;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final SnackRepository snackRepository;
    private final OrderRespository orderRepository;
//    private final UserRepository userRepository;

    @Transactional
    public OrderResponse enterOrder(Long snackId) {
        // 선착순 구매
        int result = snackRepository.purchaseSnack(snackId);

        if (result == 0) {
            // 재고가 없거나, 다른 사람이 먼저 채감
            throw new IllegalStateException("재고가 소진되었습니다.");
        }

        // Order을 만들어서 DB에 저장
        Snack snack = snackRepository.findById(snackId).get();
        // User user = userRepository.findById(userId).get();

        Order order = Order.builder()
                .snack(snack)
                .status(Order.OrderStatus.COMPLETED)
                // .user(user)
                .build();

        Order savedOrder = orderRepository.save(order);

        return OrderResponse.from(savedOrder);
    }
}