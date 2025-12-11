package urecagroup1backend.oauth;

import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import urecagroup1backend.token.domain.RefreshToken;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/*
@file JwtTokenProvider.java
@author 신형서
@since 2025-12-11
@description SpringSecurity에서 JWT 생성, 관리, 검증
*/

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private final String secretKey;

    @Value("${jwt.access-expiration}")
    private final int ACCESS_EXPIRATION;

    @Value("${jwt.refresh-expiration}")
    private final int REFRESH_EXPIRATION;

    private SecretKey SECRET_KEY;

    private final TokenService tokenService;

    @PostConstruct
    public void setSecretKey() {
        this.SECRET_KEY =  new SecretKeySpec(Base64.getDecoder().decode(secretKey), SignatureAlgorithm.HS512.getJcaName());
    }


    // accessToken 발급
    public String createAccessToken(Authentication authentication) {
        return createToken(authentication, ACCESS_EXPIRATION);
    }

    // refreshToken 발급
    public void createRefreshToken(Authentication authentication, String accessToken) {
        String refreshToken = createToken(authentication, REFRESH_EXPIRATION);
        tokenService.saveOrUpdate(authentication.getName(), refreshToken, accessToken); // redis에 저장
    }

    // Token 발급
    public String createToken(Authentication authentication, int expiration) {
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + expiration * 1000L * 60); // 밀리초 단위로 변경

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining());

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim("ROLE", authorities)
                .setIssuedAt(now)
                .setExpiration(expiredDate)
                .signWith(SECRET_KEY)
                .compact();
    }


    // 인증 정보 추출
    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        List<SimpleGrantedAuthority> authorities = getAuthorities(claims);

        // 2. security의 User 객체 생성 (인증 객체 생성)
        User principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    // 권한 확인
    private List<SimpleGrantedAuthority> getAuthorities(Claims claims) {
        return Collections.singletonList(new SimpleGrantedAuthority(
                claims.get("ROLE").toString()));
    }

    // accessToken 만료 시 RefreshToken 이용해 재발급
    public String reissueAccessToken(String accessToken) {
        if(StringUtils.hasText(accessToken)) {

            // 저장된 Refresh Token 정보 Redis에서 찾음
            RefreshToken refreshToken = tokenService.findByAccessTokenOrThrow(accessToken);
            // String refreshToken = token.getRefreshToken();

            // Refresh Token 만료 여부 검ㅈ사
            if(validateToken(refreshToken)) {
                String reissueAccessToken = createAccessToken(getAuthentication(refreshToken));
                tokenService.updateToken(reissueAccessToken, token);
                return reissueAccessToken;
            }
        }

        return null;
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        if(!StringUtils.hasText(token)) {
            return false;
        }

        Claims claims = parseClaims(token);
        return claims.getExpiration().after(new Date());
    }

    // 토큰 파싱
    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) { // 토큰 만료
            return e.getClaims();
        } catch (MalformedJwtException e) {
            throw new IllegalArgumentException("토큰 형식이 잘못됨");
        } catch (SecurityException e) {
            throw new IllegalArgumentException("서명 유효하지 않음");
        }
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }


}
