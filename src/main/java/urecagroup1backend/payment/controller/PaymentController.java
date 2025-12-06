/*
@file PaymentController.java
@author 허영현
@version 1.0
@since 2025-12-07
@description 토스 결제 승인을 요청하는 컨트롤러 입니다.
*/

package urecagroup1backend.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import urecagroup1backend.config.ApiResponse;
import urecagroup1backend.member.domain.CustomUserDetails;
import urecagroup1backend.payment.controller.docs.PaymentControllerDocs;
import urecagroup1backend.payment.dto.PaymentConfirmRequest;
import urecagroup1backend.payment.service.PaymentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
public class PaymentController implements PaymentControllerDocs {

    private final PaymentService paymentService;

    @PostMapping
    public ApiResponse<Void> confirm(PaymentConfirmRequest request,
                                     @AuthenticationPrincipal CustomUserDetails user) {

        paymentService.confirmPayment(request, user.getId());

        return new ApiResponse<>(HttpStatus.OK, "결제 승인 성공");
    }
}
