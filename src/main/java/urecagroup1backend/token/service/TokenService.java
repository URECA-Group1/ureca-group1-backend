package urecagroup1backend.token.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import urecagroup1backend.token.repository.TokenRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class TokenService {
    private final TokenRepository tokenRepository;

    // 로그아웃할 때 RefreshToken 삭제
    public void deleteRefreshToken(String memberId) {
        tokenRepository.deleteById(memberId);
    }

//    public void saveOrUpdate(String memberId, String refreshToken, String accessToken) {
//        RefreshToken token = tokenRepository.findBy
//    }
}
