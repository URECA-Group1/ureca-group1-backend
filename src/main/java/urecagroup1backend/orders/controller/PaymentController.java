package urecagroup1backend.orders.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import urecagroup1backend.orders.dto.PaymentResponse;
import urecagroup1backend.orders.service.PaymentService;

@Tag(
        name = "간식 결제",
        description = "간식 결제 및 결제 취소 API"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/snacks/payments")
public class PaymentController {

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
}
