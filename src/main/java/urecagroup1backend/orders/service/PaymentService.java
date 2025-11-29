package urecagroup1backend.orders.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import urecagroup1backend.orders.dto.PaymentResponse;
import urecagroup1backend.orders.domain.SnackOrder;
import urecagroup1backend.orders.repository.SnackOrderRepository;
import urecagroup1backend.snacks.repository.Snack;
import urecagroup1backend.snacks.repository.SnackRepository;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final SnackRepository snackRepository;
    private final SnackOrderRepository snackOrderRepository;

    // 결제 요청
    public PaymentResponse pay(Long snackId) {

        Snack snack = snackRepository.findById(snackId)
                .orElseThrow(() -> new IllegalArgumentException("간식 없음"));

        /// 포인트 부족 로직 구현 (결제 실패)
//        if (user.getPoints() < snack.getPrice()) {
//            createOrder(snack, SnackOrder.OrderStatus.FAIL);
//            return new PaymentResponse("FAIL", "포인트가 부족합니다.");
//        }

        /// 결제 성공 시
        /// 포인트 차감 로직 구현

        // 간식 주문 내역 저장
        SnackOrder order = SnackOrder.builder()
                .snack(snack)
                .status(SnackOrder.OrderStatus.COMPLETED)
                .build();

        snackOrderRepository.save(order);

        return new PaymentResponse("SUCCESS", "결제가 완료되었습니다.");
    }

    // 결제 취소
    @Transactional
    public PaymentResponse cancelPayment(Long snackId) {
        Snack snack = snackRepository.findById(snackId)
                .orElseThrow(() -> new IllegalArgumentException("간식 없음"));

        // 재고 복구
        snack.setStatus(true);

        return new PaymentResponse("SUCCESS", "재고 복구 완료.");
    }
}
