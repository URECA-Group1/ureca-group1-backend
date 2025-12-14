package urecagroup1backend.common.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;

/*
@file CookieUtil.java
@author 신형서
@since 2025-12-14
@description 쿠키 관련 Util 클래스 (토큰 위해서)
*/

public class CookieUtil {
    // 하드코딩된 값이라 바뀔 수도
    private static final String COOKIE_DOMAIN = ".urecastudycafe.store";

    public static Cookie getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return cookie;
                }
            }
        }
        return null;
    }

    public static String resolveCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static ResponseCookie createCookie(String key, String token, Boolean httpOnly, int maxAge) {
        return ResponseCookie.from(key, token)
                    .path("/")
                    .domain(COOKIE_DOMAIN)
                    .secure(true)
                    .httpOnly(httpOnly)
                    .sameSite("None")
                    .maxAge(maxAge)
                    .build();
    }

    public static ResponseCookie deleteCookie(String key, Boolean httpOnly) {
        return ResponseCookie.from(key, "")
                .path("/")
                .domain(COOKIE_DOMAIN)
                .secure(true)
                .httpOnly(httpOnly)
                .sameSite("None")
                .maxAge(0)
                .build();
    }
}
