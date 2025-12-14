package urecagroup1backend.common.exception;

/**
 * @file InvalidTokenException.java
 * @author 신형서
 * @since 2025-12-13
 * @description 토큰 관련 Custom Exception
 */

public class InvalidTokenException extends RuntimeException{
    public InvalidTokenException(String message) {
        super(message);
    }
}
