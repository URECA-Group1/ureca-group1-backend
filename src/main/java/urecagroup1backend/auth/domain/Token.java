package urecagroup1backend.auth.domain;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

/*
@file Token.java
@author 신형서
@since 2025-12-11
@description Redis에 토큰 검증을 위해 (회원id, 액세스 토큰, 리프레시 토큰) 저장
*/

@Getter
@AllArgsConstructor
@RedisHash(value = "token", timeToLive = 60 * 60 * 24 * 7)
public class Token {
    @Id
    private Long id;

    @Indexed
    private String accessToken;

    private String refreshToken;
}
