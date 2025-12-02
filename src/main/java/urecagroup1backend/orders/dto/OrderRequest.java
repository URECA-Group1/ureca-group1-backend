package urecagroup1backend.orders.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record OrderRequest(
        @Schema(description = "주문할 간식 ID")
        Long snackId

) {
}
