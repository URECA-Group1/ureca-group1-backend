package urecagroup1backend.payment.dto;

/*
@file PaymentConfirmRequest.java
@author 허영현
@version 1.0
@since 2025-12-07
@description 프론트에서 백엔드로 보내는 토스 결제 승인 요청 DTO 파일 입니다.
*/

public record PaymentConfirmRequest (
        String paymentKey,
        String orderId,
        Long amount
) {}
