package urecagroup1backend.orders.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import urecagroup1backend.orders.dto.PaymentResponse;
import urecagroup1backend.orders.service.PaymentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/snacks/payments")
public class PaymentController {

    private final PaymentService paymentService;

    // 결제 요청
    @PostMapping("/{snackId}")
    public PaymentResponse pay(@PathVariable Long snackId) {
        return paymentService.pay(snackId);
    }

    // 결제 취소 
    @PostMapping("/cancel/{snackId}")
    public PaymentResponse cancelPayment(@PathVariable Long snackId) {
        return paymentService.cancelPayment(snackId);
    }
}
