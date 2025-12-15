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
import urecagroup1backend.common.util.CookieUtil;
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
        // 프론트에서 쿠키에서 액세스 토큰 추출해 localStorage에 저장 후 쿠키에서 삭제하는 방법 선택 (Header로 보내면 최초 로그인 시 읽을 수 없음)

        ResponseCookie accessCookie = CookieUtil.createCookie("access", accessToken, false, 60);
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        System.out.println("accessCookie = " + accessCookie);

        ResponseCookie refreshCookie = CookieUtil.createCookie("refresh", refreshToken, true, jwtTokenProvider.getREFRESH_EXPIRATION());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        // oauth callback 페이지로 리다이렉트
        response.sendRedirect(redirectUrl + "/oauth/success");
    }
}
