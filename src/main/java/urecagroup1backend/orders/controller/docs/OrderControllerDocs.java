package urecagroup1backend.orders.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import urecagroup1backend.member.domain.CustomUserDetails;
import urecagroup1backend.orders.dto.OrderResponse;

import java.util.List;

@Tag(name = "주문", description = "간식 주문 및 결제 관련 API")
public interface OrderControllerDocs {

    @Operation(
            summary = "간식 주문 요청 (Kafka 비동기 접수)",
            description = "사용자의 주문 요청을 받아 대기열(Kafka)에 등록합니다. \n\n" +
                    "**주의:** 요청 즉시 주문이 생성되는 것이 아니며, '접수' 상태만 반환됩니다. \n" +
                    "실제 성공 여부는 잠시 후 **[결제 내역 조회]** API를 통해 확인해야 합니다."
    )
    @Parameter(
            name = "snackId",
            description = "구매하려는 간식의 고유 ID",
            example = "1",
            required = true
    )
    @ApiResponse(
            responseCode = "200",
            description = "주문 요청 접수 성공 (처리 중)",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "status": 200,
                                      "message": "주문 요청이 정상적으로 접수되었습니다.",
                                      "data": "SUCCESS"
                                    }
                                    """
                    )
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 (로그인 정보 없음 등)",
            content = @Content(
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "status": 400,
                                      "message": "로그인 정보가 유효하지 않습니다.",
                                      "data": null
                                    }
                                    """
                    )
            )
    )

    urecagroup1backend.config.ApiResponse<String> enterOrder(
            @PathVariable Long snackId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user
    );


    @Operation(
            summary = "결제 승인(최종 확정)",
            description = "생성된 주문(orderId)에 대해 최종 결제를 수행합니다. 주문 상태가 SUCCESS에서 PAID로 변경됩니다."
    )
    @Parameter(
            name = "orderId",
            description = "결제할 주문의 고유 ID",
            example = "15",
            required = true
    )
    @ApiResponse(
            responseCode = "200",
            description = "결제 성공",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "status": 200,
                                      "message": "결제 성공",
                                      "data": {
                                        "orderId": 15,
                                        "userId": 1,
                                        "snackName": "몽쉘 카카오",
                                        "totalPrice": 500,
                                        "orderStatus": "PAID",
                                        "orderTime": "2024-12-01T14:30:00"
                                      }
                                    }
                                    """
                    )
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 주문",
            content = @Content(
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "status": 404,
                                      "message": "해당 주문을 찾을 수 없습니다.",
                                      "data": null
                                    }
                                    """
                    )
            )
    )
    urecagroup1backend.config.ApiResponse<OrderResponse> processPayment(
            @PathVariable Long orderId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user
    );

    @Operation(
            summary = "결제 내역 조회",
            description = "사용자의 전체 주문/결제 내역 목록을 조회합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "내역 조회 성공",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "status": 200,
                                      "message": "결제 내역 조회 성공",
                                      "data": [
                                        {
                                          "orderId": 15,
                                          "userId": 1,
                                          "snackName": "몽쉘 카카오",
                                          "totalPrice": 500,
                                          "orderStatus": "SUCCESS",
                                          "orderTime": "2024-12-01T14:30:00"
                                        }
                                      ]
                                    }
                                    """
                    )
            )
    )
    urecagroup1backend.config.ApiResponse<List<OrderResponse>> getOrderHistory(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user
    );

    @Operation(
            summary = "결제 취소",
            description = "특정 주문(orderId)을 취소 처리합니다. 주문 상태가 CANCELED로 변경됩니다."
    )
    @Parameter(
            name = "orderId",
            description = "취소할 주문의 고유 ID",
            example = "15",
            required = true
    )
    @ApiResponse(
            responseCode = "200",
            description = "결제 취소 성공",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "status": 200,
                                      "message": "결제 취소 성공",
                                      "data": {
                                        "orderId": 15,
                                        "userId": 1,
                                        "snackName": "몽쉘 카카오",
                                        "totalPrice": 500,
                                        "orderStatus": "CANCELED",
                                        "orderTime": "2024-12-01T14:30:00"
                                      }
                                    }
                                    """
                    )
            )
    )
    urecagroup1backend.config.ApiResponse<OrderResponse> cancelOrder(
            @PathVariable Long orderId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user
    );
}