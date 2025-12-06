/*
@file PaymentControllerDocs.java
@author 허영현
@version 1.0
@since 2025-12-07
@description 토스 결제 승인 Swagger API 문서입니다.
*/
package urecagroup1backend.payment.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import urecagroup1backend.config.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import urecagroup1backend.payment.dto.PaymentConfirmRequest;

@Tag(name = "토스 결제", description = "토스 결제 승인 API")
public interface PaymentControllerDocs {

    @Operation(
            summary = "결제 승인",
            description = "토스 결제 승인 요청을 처리하고 성공 시 포인트를 충전합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "결제 승인 성공",
            content = @Content(
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "status": 200,
                                      "message": "결제 승인 성공",
                                      "data": null
                                    }
                                    """
                    )
            )
    )
    ApiResponse<Void> confirm(
            @RequestBody(
                    required = true,
                    description = "결제 승인 요청 정보",
                    content = @Content(
                            schema = @Schema(implementation = PaymentConfirmRequest.class),
                            examples = @ExampleObject(
                                    name = "결제 승인 요청 예시",
                                    value = """
                                            {
                                              "paymentKey": "pay_abc123",
                                              "orderId": "charge_123456",
                                              "amount": 10000
                                            }
                                            """
                            )
                    )
            )
            final PaymentConfirmRequest request
    );
}
