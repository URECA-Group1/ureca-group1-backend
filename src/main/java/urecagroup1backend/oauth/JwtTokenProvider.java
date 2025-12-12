package urecagroup1backend.oauth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import urecagroup1backend.member.domain.CustomUserDetails;
import urecagroup1backend.member.dto.MemberDto;
import urecagroup1backend.oauth.domain.CustomOAuth2User;
import urecagroup1backend.member.domain.SocialType;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;
import java.util.stream.Collectors;

/*
@file JwtTokenProvider.java
@author 신형서
@since 2025-12-11
@description 액세스 토큰, 리프레시 토큰 생성, CustomUserDetails 기반 인증 객체 생성
*/

@Component
public class JwtTokenProvider {

    private final String secretKey;
    private final int ACCESS_EXPIRATION;
    private final int REFRESH_EXPIRATION;
    private final SecretKey SECRET_KEY;
    // private final TokenService tokenService; // 사용하지 않는 의존성은 주석 처리 또는 제거

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey,
                            @Value("${jwt.access-expiration}") int accessExpiration,
                            @Value("${jwt.refresh-expiration}") int refreshExpiration) {
        this.secretKey = secretKey;
        ACCESS_EXPIRATION = accessExpiration;
        REFRESH_EXPIRATION = refreshExpiration;
        // Base64 디코딩 후 SecretKeySpec 생성
        this.SECRET_KEY = new SecretKeySpec(Base64.getDecoder().decode(secretKey), SignatureAlgorithm.HS512.getJcaName());
    }

    // accessToken 발급
    public String createAccessToken(Authentication authentication) {
        return createToken(authentication, ACCESS_EXPIRATION);
    }

    // refreshToken 발급
    public String createRefreshToken(Authentication authentication, String accessToken) {
        return createToken(authentication, REFRESH_EXPIRATION);
    }

    // Token 발급
    public String createToken(Authentication authentication, int expiration) {
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + expiration * 1000L * 60);

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining());

        // CustomUserDetails의 정보를 Claims에 추가
        Object principal = authentication.getPrincipal();
        String name = "";
        String profileUrl = "";
        String socialType = "";

        if (principal instanceof CustomUserDetails) {
            CustomUserDetails customUser = (CustomUserDetails) principal;
            name = customUser.getName();
            profileUrl = customUser.getProfile();
            socialType = customUser.getSocialType() != null ? customUser.getSocialType().name() : "";
        }

        return Jwts.builder()
                .setSubject(authentication.getName()) // 보통 사용자 ID 또는 Email
                .claim("ROLE", authorities)
                .claim("name", name)
                .claim("profile", profileUrl)
                .claim("socialType", socialType)
                .setIssuedAt(now)
                .setExpiration(expiredDate)
                .signWith(SECRET_KEY, SignatureAlgorithm.HS512)
                .compact();
    }

    // 인증 정보 추출 (Principal: CustomUserDetails)
    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);

        // 1. Claims에서 MemberDto에 필요한 정보 추출
        String email = claims.getSubject(); // CustomUserDetails의 Email 필드로 사용
        String name = claims.get("name", String.class);
        String profileUrl = claims.get("profile", String.class);
        String socialTypeStr = claims.get("socialType", String.class);
        String role = claims.get("ROLE", String.class);

        // 2. MemberDto 생성 (DTO가 필드 주입 가능한 구조라고 가정)
        MemberDto memberDto = new MemberDto();
        memberDto.setEmail(email);
        memberDto.setName(name);
        memberDto.setProfileUrl(profileUrl);

        // SocialType 처리 (enum 변환 시 예외 방지)
        try {
            memberDto.setSocialType(SocialType.valueOf(socialTypeStr.toUpperCase()));
        } catch (IllegalArgumentException | NullPointerException ignored) {
            // SocialType이 Claims에 없거나 유효하지 않은 경우 처리
        }

        // 3. CustomUserDetails 생성 (Principal 객체)
        CustomUserDetails principal = new CustomUserDetails(memberDto);

        // 4. 권한 설정
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(role));

        // UsernamePasswordAuthenticationToken을 사용하여 Authentication 객체 반환
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    // Request Header에서 토큰 추출 (Filter에서 호출될 메서드)
    public String resolveToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        if(!StringUtils.hasText(token)) {
            return false;
        }

        // 토큰 파싱 시 만료되지 않았으면 true 반환
        try {
            Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY) // SECRET_KEY 사용
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            // 만료된 토큰은 여기서 예외 발생 (Filter에서 처리)
            return false;
        } catch (Exception e) {
            // 그 외 다른 문제 (유효하지 않은 서명, 형식 등)
            return false;
        }
    }

    // 토큰 파싱 (예외 발생 시 Claims 반환)
    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY) // SECRET_KEY 사용
                    .build()
                    .parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims(); // 만료된 경우에도 Claims는 반환
        } catch (MalformedJwtException e) {
            throw new IllegalArgumentException("토큰 형식이 잘못됨", e);
        } catch (SecurityException e) {
            throw new IllegalArgumentException("서명이 유효하지 않음", e);
        }
    }
}