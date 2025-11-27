package urecagroup1backend.snacks.dto;

public record SnackResponse(
    Long id,
    String snack_name,
    int snack_price,
    boolean snack_status
){}
