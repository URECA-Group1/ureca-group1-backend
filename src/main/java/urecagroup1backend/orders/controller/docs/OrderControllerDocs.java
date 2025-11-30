package urecagroup1backend.orders.controller.docs;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import urecagroup1backend.orders.dto.OrderResponse;

@Tag(name = "주문", description = "간식 주문 및 결제 관련 API")
public interface OrderControllerDocs {

    @Operation(
            summary = "간식 주문(구매) 진입",
            description = "특정 간식(snackId)을 선택하여 주문을 생성합니다. 재고가 없거나(status=false) 이미 주문된 간식은 구매할 수 없습니다."
    )
    @Parameter(
            name = "snackId",
            description = "구매하려는 간식의 고유 ID",
            example = "1",
            required = true
    )
    @ApiResponse(
            responseCode = "201",
            description = "주문 생성 성공",
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
                                        "snackPrice": 500,
                                        "orderStatus": "FAIL",
                                        "orderTime": "2024-11-28T14:30:00"
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
    @Operation(summary = "주문 취소", description = "주문를 취소하거나 실패했을 때, 간식의 재고를 다시 판매중으로 복구합니다.")
    @ApiResponse(responseCode = "200", description = "재고 복구 성공")
    @PostMapping("/{snackId}/cancel")
    urecagroup1backend.config.ApiResponse<Void> cancelOrder(@PathVariable Long snackId);
}
