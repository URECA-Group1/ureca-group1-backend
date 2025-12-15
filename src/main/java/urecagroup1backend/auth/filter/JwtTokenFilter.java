package urecagroup1backend.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import urecagroup1backend.auth.provider.JwtTokenProvider;

import java.io.IOException;

/*
@file JwtTokenFilter.java
@author 신형서
@since 2025-12-11
@description Jwt 추출 후 유효성 검사, 검증 성공 시 사용자 인증 객체 SecurityContext에 설정
*/

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    //
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        return path.startsWith("/oauth2/")
                || path.startsWith("/login/oauth2/")
                || path.equals("/favicon.ico");
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        System.out.println(">>> [DEBUG LOG] JwtTokenFilter.doFilterInternal 실행됨. 요청 URI: " + request.getRequestURI());
        log.info("[log] AccessToken 검증 시작");

        // 1. JWT 토큰 추출
        String jwtToken = jwtTokenProvider.resolveToken(request);
        log.info("[log] Extracted Token: " + (jwtToken != null ? jwtToken.substring(0, 15) + "..." : "NULL"));

        try {
            if (jwtToken != null) {
                // 2. 토큰 유효성 검증
                // validateToken이 false를 반환하면 (e.g. 만료) 여기서 Authentication 설정하지 않음.
                if (jwtTokenProvider.validateToken(jwtToken)) {
                    log.info("[log] Token Validation: SUCCESS");
                    // 3. 검증 성공 시, 인증 객체 생성 및 SecurityContext에 설정 (Principal: CustomUserDetails)
                    setAuthentication(jwtToken);
                    log.info("[log] SecurityContext Status: AUTHENTICATED");
                }
                else {
                    log.warn("[log] Token Validation: FAILED (Expired or Invalid Signature)");
                }
            }

            // 4. 다음 필터로 진행
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            // 5. 토큰 파싱 또는 서명 오류 등 예외 처리 (401 응답)
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            // 응답 메시지
            response.getWriter().write("{\"error\": \"Invalid or corrupted token.\"}");
        }
    }

    // SecurityContextHolder에 Authentication 객체를 설정합니다.
    private void setAuthentication(String jwtToken) {
        // Provider를 사용하여 토큰에서 CustomUserDetails를 Principal로 갖는 인증 객체 생성
        Authentication authentication = jwtTokenProvider.getAuthentication(jwtToken);

        // SecurityContext에 설정
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}