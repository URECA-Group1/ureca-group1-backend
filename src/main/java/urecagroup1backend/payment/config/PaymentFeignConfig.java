/*
@file PaymentFeignConfig.java
@author 허영현
@version 1.0
@since 2025-12-07
@description 토스 결제 API를 호출할 때만 적용되는 커스텀 설정 파일입니다.
*/
package urecagroup1backend.payment.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
public class PaymentFeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {

            String secretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";

            // Base64 인코딩 (secretKey + ":")
            String encoded = Base64.getEncoder()
                    .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));

            template.header("Authorization", "Basic " + encoded);
            template.header("Content-Type", "application/json");
        };
    }
}
