package urecagroup1backend.payments.dto;

import urecagroup1backend.payments.domain.Payment;

import java.time.LocalDateTime;

public record PaymentListResponse(
    Long orderId,
    String snackName,
    int snackPrice,
    String orderStatus,
    LocalDateTime orderTime
)
{
    public static PaymentListResponse from(Payment payment){
        return new PaymentListResponse(
                payment.getId(),
                payment.getSnack().getName(),
                payment.getSnack().getPrice(),
                payment.getStatus().name(),
                payment.getCreatedAt()
        );
    }
}
