package urecagroup1backend.orders.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import urecagroup1backend.orders.Reposiotry.Order;

import java.time.LocalDateTime;

public record OrderResponse(
        @Schema(description = "주문 고유 번호")
        Long orderId,

        @Schema(description = "구매자 ID")
        Long userId,

        @Schema(description = "간식 이름")
        String snackName,

        @Schema(description = "결제 금액")
        int totalPrice,

        @Schema(description = "주문 상태 (COMPLETED, FAIL)")
        String orderStatus,

        @Schema(description = "주문 일시")
        LocalDateTime orderTime
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                null, // User
                order.getSnack().getName(),
                order.getSnack().getPrice(),
                order.getStatus().name(),
                order.getCreatedAt()
        );
    }
}
