package urecagroup1backend.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import urecagroup1backend.config.ApiResponse;

/**
 * @file ApiControllerAdvice
 * @author 윤재민
 * @since 2025-12-04
 * @description 예외에 대한 응답을 내려주는 파일입니다.
 */
@RestControllerAdvice
public class ApiControllerAdvice {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {
        log.error("Exception : {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(HttpStatus.BAD_REQUEST, e.getMessage()));
    }

    // 토큰 관련 예외 처리
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiResponse<?>> handleInvalidTokenException(InvalidTokenException e) {
        log.warn("Invalid Token Exception: {}", e.getMessage());
        // JWT/Refresh Token 문제(재로그인 필요)는 401 Unauthorized 또는 400 Bad Request
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED) // 💡 401 Unauthorized 권장
                .body(new ApiResponse(HttpStatus.UNAUTHORIZED, e.getMessage()));
    }
}
