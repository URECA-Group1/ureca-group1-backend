package urecagroup1backend.orders.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import urecagroup1backend.config.ApiResponse;
import urecagroup1backend.orders.dto.OrderResponse;

import java.util.List;

@Tag(name = "간식 주문 내역", description = "간식 주문 내역 관리 API")
public interface OrderControllerDocs {

    @Operation(
            summary = "간식 주문 내역 조회",
            description = "사용자의 간식 주문 내역 전체를 조회합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "주문 내역 조회 성공",
            content = @Content(
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "status": 200,
                                      "message": "주문 내역 조회 성공",
                                      "data": [
                                        {
                                          "orderId": 1,
                                          "snackName": "콜라",
                                          "snackPrice": 1500,
                                          "orderStatus": "COMPLETED",
                                          "orderTime": "2025-02-18T11:08:00"
                                        },
                                        {
                                          "orderId": 2,
                                          "snackName": "빵",
                                          "snackPrice": 2000,
                                          "orderStatus": "COMPLETED",
                                          "orderTime": "2025-02-18T14:00:00"
                                        },
                                        {
                                          "orderId": 3,
                                          "snackName": "빵",
                                          "snackPrice": 2000,
                                          "orderStatus": "FAIL",
                                          "orderTime": "2025-02-18T14:22:00"
                                        }
                                      ]
                                    }
                                    """
                    )
            )
    )
    ApiResponse<List<OrderResponse>> getOrders();
}
