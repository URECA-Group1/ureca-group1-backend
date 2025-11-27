package urecagroup1backend.config;

import org.springframework.http.HttpStatus;

public record ApiResponse<T>(
        int status,
        String message,
        T data
) {
    public ApiResponse(HttpStatus status, String message, T data) {
        this(status.value(), message, data);
    }

    public ApiResponse(HttpStatus status, String message) {
        this(status.value(), message, null);
    }
}