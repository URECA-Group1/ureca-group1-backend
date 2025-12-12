package urecagroup1backend.oauth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import urecagroup1backend.oauth.domain.CustomOAuth2User;

import java.io.IOException;

/*
@file OAuth2SuccessHandler.java
@author 신형서
@since 2025-12-11
@description 로그인 성공적으로 끝나면 호출되는 Handler
*/

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final String URI = "/oauth2/success";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        // accessToken, refreshToken 발급
        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication, accessToken);

        // 쿠키에 accessToken 담아서 전달
        response.addCookie(createCookie("access", accessToken));
        response.addCookie(createCookie("refresh", refreshToken));

        // 리프레시 토큰 업데이트 필요

        // 하드코딩 수정 필요
        String redirectUrl = UriComponentsBuilder.fromUriString("http://localhost:3000")
                .build().toUriString();

        // 리다이렉트
        response.sendRedirect(redirectUrl);
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setPath("/");
        // cookie.setSecure(true); // https 에서만 전송 (운영환경에서만)
        cookie.setHttpOnly(true); // 클라이언트 속 JS 접근 불가 (XSS 방어)
        // cookie.setMaxAge(); // 만료시간 : AccessToken 유효기간과 동일하게 맞추기

        return cookie;


    }
}
