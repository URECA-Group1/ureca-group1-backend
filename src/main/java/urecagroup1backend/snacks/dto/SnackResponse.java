package urecagroup1backend.snacks.dto;

import urecagroup1backend.snacks.domain.Snack;

public record SnackResponse(
    Long snackId,
    String snackName,
    int snackPrice,
    int snackQuantity
){
    public static SnackResponse from(Snack snack) {
        return new SnackResponse(
                snack.getId(),
                snack.getName(),
                snack.getPrice(),
                snack.getQuantity()
        );
    }
}
