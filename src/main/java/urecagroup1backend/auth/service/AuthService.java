package urecagroup1backend.auth.service;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import urecagroup1backend.member.domain.CustomUserDetails;
import urecagroup1backend.member.domain.Member;
import urecagroup1backend.member.dto.MemberDto;
import urecagroup1backend.auth.dto.TokenResDto;
import urecagroup1backend.member.repository.MemberRepository;
import urecagroup1backend.auth.provider.JwtTokenProvider;
import urecagroup1backend.auth.domain.Token;
import urecagroup1backend.auth.repository.TokenRepository;

import java.util.Optional;

/*
@file TokenRepository.java
@author 신형서
@since 2025-12-11
@description 로그아웃, 토큰 저장, 토큰 재발급 비즈니스 로직하는 서비스
*/

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final TokenRepository tokenRepository;

    // 로그아웃
    @Transactional
    public void logOut(Long memberId) {
        if(tokenRepository.existsById(memberId)) {
            tokenRepository.deleteById(memberId);
            log.info("로그아웃 성공 : Refresh Token 삭제 완료 Member ID : {}", memberId);
        }
        else {
            log.info("로그아웃 시도 : Redis에 유효한 Refresh Token이 없습니다. Member ID : {}", memberId);
        }
    }

    @Transactional
    public TokenResDto saveToken(Long memberId, String accessToken, String refreshToken) {
        log.info("Redis에 Refresh Token 저장 시작. Member ID: {}", memberId);
        Token token = new Token(memberId, accessToken, refreshToken);

        Token saved = tokenRepository.save(token);

        log.info("Redis에 Refresh Token 저장 완료.");

        return TokenResDto.builder()
                .accessToken(saved.getAccessToken())
                .refreshToken(saved.getRefreshToken())
                .build();
    }

    // 토큰 재발급
    @Transactional
    public TokenResDto reissueToken(String refreshToken) {
        // RefreshToken 유효성 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("리프레시 토큰이 유효하지 않음");
        }

        // Refresh Token에서 사용자 정보 추출
        Claims claims = jwtTokenProvider.parseClaims(refreshToken);
        Long id = claims.get("memberId", Long.class);

        if(id == null || id == 0L) { // ID가 null이거나 0이면 오류
            String subjectUsed = claims.getSubject();
            log.error("Refresh Token의 ID가 누락되었습니다. Subject 값: {}", subjectUsed);

            throw new IllegalArgumentException("토큰에서 Member ID를 찾을 수 없습니다.");
        }

        // Redis에 저장된 토큰과 일치 여부 확인
        Optional<Token> storedToken = tokenRepository.findById(id);

        // 저장된 리프레시 토큰 없거나 값이 다르면
        if(storedToken.isEmpty()) {
            log.warn("Redis 저장소의 Refresh Token 만료. Member ID: {}", id);
            // 보안: 만약 불일치하면 DB와 Redis의 토큰 쌍을 모두 삭제하여 탈취된 토큰으로 인한 피해 확산 방지 로직 필요
            throw new IllegalArgumentException("Redis 저장소의 Refresh Token 만료.");
        }

        log.info("[log] 리프레시토큰 id: {}, 저장소 : {}, 방금: {}", storedToken.get().getId(), storedToken.get().getRefreshToken(), refreshToken);

        if(!storedToken.get().getRefreshToken().equals(refreshToken)) {
            log.warn("Redis 저장소의 Refresh Token 불일치. Member ID: {}", id);
            // 보안: 만약 불일치하면 DB와 Redis의 토큰 쌍을 모두 삭제하여 탈취된 토큰으로 인한 피해 확산 방지 로직 필요
            throw new IllegalArgumentException("Redis 저장소의 Refresh Token 불일치");
        }

        // 새로운 토큰 발급을 위한 AUthentication 객체 생성
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 회원입니다."));

        MemberDto memberDto = MemberDto.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .profileUrl(member.getProfileImgUrl())
                .socialType(member.getSocialType())
                .build();

        CustomUserDetails userDetails = new CustomUserDetails(memberDto);
        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        // 5. Access Token 재발급
        String newAccessToken = jwtTokenProvider.createAccessToken(newAuthentication);

        // 6. Refresh Token도 재발급 (Rolling Refresh Token 패턴)
        String newRefreshToken = jwtTokenProvider.createRefreshToken(newAuthentication);

        // 7. Redis 저장소 갱신 (기존 토큰 무효화 및 새 토큰 저장)
        tokenRepository.deleteById(id); // 기존 토큰 삭제
        saveToken(id, newAccessToken, newRefreshToken); // 새 토큰 저장

        log.info("Access Token 재발급 및 Refresh Token 갱신 완료. Member ID: {}", id);

        // 8. 새로 발급된 토큰 반환
        return new TokenResDto(newAccessToken, newRefreshToken);
    }



}