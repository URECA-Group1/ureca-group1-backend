package urecagroup1backend.token.domain;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

@Getter
@AllArgsConstructor
@RedisHash(value = "refresh_token", timeToLive = 60 * 60 * 24 * 7)
public class RefreshToken {
    @Id
    private String id;

    private String token;

    public RefreshToken updateRefreshToken(String token) {
        this.token = token;
        return this;
    }
}
