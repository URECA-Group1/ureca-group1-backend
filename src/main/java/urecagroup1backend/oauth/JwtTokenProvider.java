package urecagroup1backend.oauth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import urecagroup1backend.member.domain.CustomUserDetails;
import urecagroup1backend.member.dto.MemberDto;
import urecagroup1backend.member.domain.SocialType;
import urecagroup1backend.oauth.domain.Token;
import urecagroup1backend.oauth.repository.TokenRepository;

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

@Slf4j
@Component
public class JwtTokenProvider {

    private final String secretKey;

    @Getter
    private final int ACCESS_EXPIRATION;

    @Getter
    private final int REFRESH_EXPIRATION;
    private final SecretKey SECRET_KEY;
    private final TokenRepository tokenRepository;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey,
                            @Value("${jwt.access-expiration}") int accessExpiration,
                            @Value("${jwt.refresh-expiration}") int refreshExpiration, TokenRepository tokenRepository) {
        this.secretKey = secretKey;
        ACCESS_EXPIRATION = accessExpiration;
        REFRESH_EXPIRATION = refreshExpiration;
        // Base64 디코딩 후 SecretKeySpec 생성
        this.SECRET_KEY = new SecretKeySpec(Base64.getDecoder().decode(secretKey), SignatureAlgorithm.HS512.getJcaName());
        this.tokenRepository = tokenRepository;
    }

    // accessToken 발급
    public String createAccessToken(Authentication authentication) {
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + ACCESS_EXPIRATION * 1000L * 60);

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining());

        // CustomUserDetails의 정보를 Claims에 추가
        Object principal = authentication.getPrincipal();
        String name = "";
        String profileUrl = "";
        String socialType = "";
        Long id = 0L;

        if (principal instanceof CustomUserDetails) {
            CustomUserDetails customUser = (CustomUserDetails) principal;
            id = customUser.getId();
            name = customUser.getName();
            profileUrl = customUser.getProfile();
            socialType = customUser.getSocialType() != null ? customUser.getSocialType().name() : "";
        }

        return Jwts.builder()
                .setSubject(authentication.getName()) // 보통 사용자 ID 또는 Email
                .claim("memberId", id)
                .claim("ROLE", authorities)
                .claim("name", name)
                .claim("profile", profileUrl)
                .claim("socialType", socialType)
                .setIssuedAt(now)
                .setExpiration(expiredDate)
                .signWith(SECRET_KEY, SignatureAlgorithm.HS512)
                .compact();
    }

    // refreshToken 발급
    public String createRefreshToken(Authentication authentication) {

        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + REFRESH_EXPIRATION * 1000L * 60);
        // CustomUserDetails의 정보를 Claims에 추가
        Object principal = authentication.getPrincipal();
        Long id = 0L;

        if (principal instanceof CustomUserDetails) {
            CustomUserDetails customUser = (CustomUserDetails) principal;
            id = customUser.getId();
        }

        // Claims claims = Jwts.claims().setSubject(Long.toString(id));
        Claims claims = Jwts.claims();
        claims.put("memberId", id);

        String refreshToken =  Jwts.builder()
                .setClaims(claims)
                .setId(UUID.randomUUID().toString()) // 매번 고유한 JTI
                .setIssuedAt(now)
                .setExpiration(expiredDate)
                .signWith(SECRET_KEY, SignatureAlgorithm.HS512)
                .compact();

        log.info("[log] createRefreshToken 호출 완료: {}", refreshToken);

        return refreshToken;
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
        Long id = claims.get("memberId", Long.class);

        // 2. MemberDto 생성 (DTO가 필드 주입 가능한 구조라고 가정)
        MemberDto memberDto = new MemberDto();
        memberDto.setEmail(email);
        memberDto.setId(id);
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

        log.info("[log] Principal ID: " + principal.getId() + ", Email: " + principal.getEmail());

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

        log.info("[log] resolveToken 결과 Null");
        return null;
    }

    public String resolveRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh".equals(cookie.getName())) { // 👈 쿠키 이름 확인
                    return cookie.getValue();
                }
            }
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
    public Claims parseClaims(String token) {
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