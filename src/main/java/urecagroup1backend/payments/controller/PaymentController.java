package urecagroup1backend.payments.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import urecagroup1backend.config.ApiResponse;
import urecagroup1backend.payments.controller.docs.PaymentControllerDocs;
import urecagroup1backend.payments.dto.PaymentListResponse;
import urecagroup1backend.payments.dto.PaymentResponse;
import urecagroup1backend.payments.service.PaymentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/snacks/payments")
public class PaymentController implements PaymentControllerDocs {

    private final PaymentService paymentService;

    // 결제 요청
    @Operation(
            summary = "간식 결제 요청",
            description = "특정 간식에 대한 결제 요청을 처리합니다."
    )
    @PostMapping("/{snackId}")
    public PaymentResponse pay(@Parameter(description = "결제 요청할 간식 ID", required = true)
                                   @PathVariable(name = "snackId") Long snackId) {
        return paymentService.pay(snackId);
    }

    // 결제 취소
    @Operation(
            summary = "결제 취소 요청",
            description = "결제 페이지에서 결제를 취소합니다. 간식의 status(재고 상태)가 true(구매 가능)로 변경됩니다."
    )
    @PostMapping("/cancel/{snackId}")
    public PaymentResponse cancelPayment(@Parameter(description = "결제 취소할 간식 ID", required = true)
                                             @PathVariable(name = "snackId") Long snackId) {
        return paymentService.cancelPayment(snackId);
    }

    // 결제 내역 조회
    @Override
    @GetMapping
    public ApiResponse<List<PaymentListResponse>> getPayments() {
        List<PaymentListResponse> payments = paymentService.getPayments();
        return new ApiResponse<>(HttpStatus.OK, "결제 내역 조회 성공", payments);
    }

    // 환불
    @Override
    @PostMapping("/refund/{orderId}")
    public ApiResponse<Void> refundPayment(@Parameter(name = "orderId", description = "환불할 주문 ID", required = true)
                                               @PathVariable("orderId") Long orderId) {
        paymentService.refundPayment(orderId);
        return new ApiResponse<>(HttpStatus.OK, "포인트가 환불되었습니다.");
    }
}
