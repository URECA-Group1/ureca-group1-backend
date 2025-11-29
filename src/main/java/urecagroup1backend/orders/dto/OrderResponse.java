package urecagroup1backend.orders.dto;

import urecagroup1backend.orders.domain.SnackOrder;

import java.time.LocalDateTime;

public record OrderResponse(
    Long orderId,
    String snackName,
    int snackPrice,
    String orderStatus,
    LocalDateTime orderTime
)
{
    public static OrderResponse from(SnackOrder order){
        return new OrderResponse(
                order.getId(),
                order.getSnack().getName(),
                order.getSnack().getPrice(),
                order.getStatus().name(),
                order.getCreatedAt()
        );
    }
}
