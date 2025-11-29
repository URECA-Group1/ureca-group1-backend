package urecagroup1backend.snacks.dto;

public record SnackRequest(
    String name,
    int price,
    Boolean status
) {}
