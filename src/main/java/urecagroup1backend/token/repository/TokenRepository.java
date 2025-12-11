package urecagroup1backend.token.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import urecagroup1backend.token.domain.RefreshToken;

import java.util.Optional;

@Repository
public interface TokenRepository extends CrudRepository<RefreshToken, String> {
    Optional<RefreshToken> findByAccessToken(String accessToken);
}
