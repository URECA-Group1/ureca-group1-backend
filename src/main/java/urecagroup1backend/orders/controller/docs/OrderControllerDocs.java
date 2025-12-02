package urecagroup1backend.orders.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import urecagroup1backend.orders.dto.OrderResponse;

import java.util.List;

@Tag(name = "주문", description = "간식 주문 및 결제 관련 API")
public interface OrderControllerDocs {

    @Operation(
            summary = "간식 주문(구매) 진입",
            description = "특정 간식(snackId)을 선택하여 가주문을 생성합니다(상태: PENDING). 재고가 없거나 유효하지 않은 간식은 주문할 수 없습니다."
    )
    @Parameter(
            name = "snackId",
            description = "구매하려는 간식의 고유 ID",
            example = "1",
            required = true
    )
    @ApiResponse(
            responseCode = "201",
            description = "주문 진입(가주문 생성) 성공",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "status": 201,
                                      "message": "구매 진입 성공",
                                      "data": {
                                        "orderId": 15,
                                        "userId": 1,
                                        "snackName": "몽쉘 카카오",
                                        "totalPrice": 500,
                                        "orderStatus": "PENDING",
                                        "orderTime": "2024-12-01T14:30:00"
                                      }
                                    }
                                    """
                    )
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "주문 실패 (재고 없음 또는 잘못된 요청)",
            content = @Content(
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "status": 400,
                                      "message": "구매 불가능한 간식입니다.",
                                      "data": null
                                    }
                                    """
                    )
            )
    )
    urecagroup1backend.config.ApiResponse<OrderResponse> enterOrder(
            @PathVariable Long snackId
    );

    @Operation(
            summary = "결제 승인(최종 확정)",
            description = "생성된 주문(orderId)에 대해 최종 결제를 수행합니다. 주문 상태가 PENDING에서 PAID로 변경됩니다."
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
            @PathVariable Long orderId
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
                                          "orderStatus": "PAID",
                                          "orderTime": "2024-12-01T14:30:00"
                                        },
                                        {
                                          "orderId": 12,
                                          "userId": 1,
                                          "snackName": "새우깡",
                                          "totalPrice": 1200,
                                          "orderStatus": "CANCELED",
                                          "orderTime": "2024-11-30T10:00:00"
                                        }
                                      ]
                                    }
                                    """
                    )
            )
    )
    urecagroup1backend.config.ApiResponse<List<OrderResponse>> getOrderHistory();

    @Operation(
            summary = "결제 취소",
            description = "특정 주문(orderId)을 취소 처리합니다. 주문 상태가 CANCELED(또는 FAIL)로 변경됩니다."
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
            @PathVariable Long orderId
    );
}