package urecagroup1backend.member.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import urecagroup1backend.config.ApiResponse;
import urecagroup1backend.member.controller.docs.MemberControllerDocs;
import urecagroup1backend.member.domain.CustomUserDetails;
import urecagroup1backend.auth.dto.TokenResDto;
import urecagroup1backend.auth.provider.JwtTokenProvider;
import urecagroup1backend.auth.service.AuthService;
import java.util.HashMap;
import java.util.Map;

/*
@file MemberController.java
@author 신형서
@since 2025-11-29
@description 회원 컨트롤러
*/

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("api/members")
public class MemberController implements MemberControllerDocs {
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthService authService;

    // 내 로그인 정보 가져오기
    @GetMapping("/me")
    public ApiResponse<?> me(@AuthenticationPrincipal CustomUserDetails user) {
        Map<String, Object> loginInfo = new HashMap<>();
        loginInfo.put("id", user.getId());
        loginInfo.put("email", user.getEmail());
        loginInfo.put("name", user.getName());

        return new ApiResponse<>(HttpStatus.OK, "내 로그인 정보", loginInfo);
    }

    // 로그아웃
    @PostMapping("/logout")
    public ApiResponse<?> logout(@AuthenticationPrincipal CustomUserDetails user) {
        Long id = user.getId();
        authService.logOut(id);
        return new ApiResponse<>(HttpStatus.OK, "로그아웃 성공", null);
    }

    // 토큰 재발급
    @PostMapping("/token/refresh")
    public ApiResponse<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtTokenProvider.resolveRefreshTokenFromCookie(request);
        TokenResDto newToken = authService.reissueToken(refreshToken);

        // 액세스 토큰은 헤더에
        response.addHeader("Authorization", "Bearer " + newToken.getAccessToken());

        // 리프레시 토큰은 쿠키에 넣어서 보내기
        Cookie refreshCookie = new Cookie("refresh", newToken.getRefreshToken());
        refreshCookie.setPath("/");
        refreshCookie.setSecure(true); // https 에서만 전송 (운영환경에서만)
        refreshCookie.setHttpOnly(true); // 클라이언트 속 JS 접근 불가 (XSS 방어)
        refreshCookie.setMaxAge(jwtTokenProvider.getREFRESH_EXPIRATION()); // 만료시간 : refreshToken 유효기간과 동일하게 맞추기
        response.addCookie(refreshCookie);

        return new ApiResponse<>(HttpStatus.OK, "AccessToken & RefreshToken갱신 완료", null);
    }
}
