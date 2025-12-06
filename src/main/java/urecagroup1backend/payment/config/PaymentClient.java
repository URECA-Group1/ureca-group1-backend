/*
@file PaymentClient.java
@author 허영현
@version 1.0
@since 2025-12-07
@description 토스 결제 승인 API 호출을 수행하는 Feign 클라이언트 파일입니다.
*/
package urecagroup1backend.payment.config;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import urecagroup1backend.payment.dto.PaymentConfirmRequest;

import java.util.Map;

@FeignClient(
        name = "paymentClient",
        url = "https://api.tosspayments.com/v1/payments",
        configuration = urecagroup1backend.payment.config.PaymentFeignConfig.class
)
public interface PaymentClient {

    @PostMapping("/confirm")
    Map<String, Object> confirmPayment(@RequestBody PaymentConfirmRequest request);
}
