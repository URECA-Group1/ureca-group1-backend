package urecagroup1backend.snacks.dto;

import urecagroup1backend.snacks.repository.Snack;

public record SnackResponse(
    Long id,
    String snackName,
    int snackPrice,
    int snackStatus
){
    public static SnackResponse from(Snack snack) {
        return new SnackResponse(
                snack.getId(),
                snack.getName(),
                snack.getPrice(),
                snack.getStatus()
        );
    }
}
