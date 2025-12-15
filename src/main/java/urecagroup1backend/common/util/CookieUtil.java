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

    // request에서 쿠키 읽기
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

    // request에서 쿠키 읽어서 string 변환 후 리턴
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

    // 쿠키 생성
    // sameSite 추가를 위해 ResponseCookie로 진행
    public static ResponseCookie createCookie(String key, String token, Boolean httpOnly, int maxAge) {
        return ResponseCookie.from(key, token)
                    .path("/")
                    //.domain(COOKIE_DOMAIN) // 임시 삭제
                    .secure(true)
                    .httpOnly(httpOnly)
                    .sameSite("None")
                    .maxAge(maxAge)
                    .build();
    }

    // 쿠키 삭제
    // sameSite 추가를 위해 ResponseCookie로 진행
    // maxAge(0)으로 쿠키 삭제
    public static ResponseCookie deleteCookie(String key, Boolean httpOnly) {
        return ResponseCookie.from(key, "")
                .path("/")
                //.domain(COOKIE_DOMAIN) // 임시 삭제
                .secure(true)
                .httpOnly(httpOnly)
                .sameSite("None")
                .maxAge(0)
                .build();
    }
}
