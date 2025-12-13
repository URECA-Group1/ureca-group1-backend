package urecagroup1backend.auth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import urecagroup1backend.auth.provider.JwtTokenProvider;
import urecagroup1backend.member.domain.CustomUserDetails;
import urecagroup1backend.auth.service.AuthService;

import java.io.IOException;

/*
@file OAuth2SuccessHandler.java
@author 신형서
@since 2025-12-11
@description 로그인 성공적으로 끝나면 호출되는 Handler
*/

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthService authService;
    private final String redirectUrl;

    public OAuth2SuccessHandler(
            JwtTokenProvider jwtTokenProvider,
            AuthService authService,
            @Value("${app.front-url}") String redirectUrl) {

        this.jwtTokenProvider = jwtTokenProvider;
        this.authService = authService;
        this.redirectUrl = redirectUrl;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        // accessToken, refreshToken 발급
        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

        // 회원 ID 추출해서 Redis에 토큰 저장
        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails customUser = (CustomUserDetails) authentication.getPrincipal();
            Long memberId = customUser.getId();

            // Redis에 토큰 저장
            authService.saveToken(memberId, accessToken, refreshToken);
        }

        // 최초 로그인 시 (액세스 토큰, 리프레시 토큰) 모두 쿠키에 담아서 프론트에 전달 후,
        // 프론트에서 쿠키에서 액세스 토큰 추출해 localStorage에 저장 후 쿠키에서 삭제하는 방법 선택

        // 액세스 토큰은 헤더에
        response.addHeader("Authorization", "Bearer " + accessToken);

        // 리프레시 토큰은 쿠키에 넣어서 보내기
        ResponseCookie refreshCookie = ResponseCookie.from("refresh", refreshToken)
                .path("/")
                .secure(true) // https 에서만 전송 (운영환경에서만)
                .httpOnly(true) // 클라이언트 속 JS 접근 불가 (XSS 방어)
                .sameSite("None") // 백엔드 - 프론트엔드 URL이 달라서 추가 필요함
                .domain(".urecastudycafe.store/") // (하드코딩 수정?) 백엔드 - 프론트엔드 모두 사용가능한 도메인 설정 필요
                .maxAge(jwtTokenProvider.getREFRESH_EXPIRATION()) // 만료시간 : refreshToken 유효기간과 동일하게 맞추기
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        // 리다이렉트
        response.sendRedirect(redirectUrl);
    }
}
