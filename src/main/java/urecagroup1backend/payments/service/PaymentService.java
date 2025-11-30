package urecagroup1backend.payments.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import urecagroup1backend.payments.domain.Payment;
import urecagroup1backend.payments.dto.PaymentListResponse;
import urecagroup1backend.payments.dto.PaymentResponse;
import urecagroup1backend.payments.repository.PaymentRepository;
import urecagroup1backend.snacks.repository.Snack;
import urecagroup1backend.snacks.repository.SnackRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final SnackRepository snackRepository;
    private final PaymentRepository paymentRepository;

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
        /// 유저 정보 추가 필요 (Member)
        Payment payment = Payment.builder()
                .snack(snack)
                .status(Payment.PaymentStatus.PAID)
                .build();

        paymentRepository.save(payment);

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

    // 간식 주문 내역 조회 (지금은 모든 내역 조회)
    /// JWT 토큰에서 추출한 유저 ID로 조회 수정 필요
    public List<PaymentListResponse> getPayments() {

        List<Payment> payments = paymentRepository.findAll();

        return payments.stream()
                .map(PaymentListResponse::from)
                .collect(Collectors.toList());
    }

    // 환불
    @Transactional
    public void refundPayment(Long orderId) {

        // 기존 결제 내역 조회
        Payment payment = paymentRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("결제 내역 없음"));

        // 재고 복구
        Snack snack = payment.getSnack();
        snack.setStatus(true);

        /// 포인트 환불 로직 (Member 연동 필요)

        // 간식 환불 내역 저장
        Payment refundPayment = Payment.builder()
                .snack(snack)
                .status(Payment.PaymentStatus.REFUNDED)
                .build();

        paymentRepository.save(refundPayment);
    }
}
