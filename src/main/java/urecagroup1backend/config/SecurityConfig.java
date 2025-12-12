package urecagroup1backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import urecagroup1backend.member.service.MemberService;
import urecagroup1backend.oauth.JwtTokenFilter;
import urecagroup1backend.oauth.OAuth2SuccessHandler;
import urecagroup1backend.oauth.service.CustomOAuth2UserService;

import java.util.Arrays;
import java.util.Map;

/*
@file SecurityConfig.java
@author 신형서
@since 2025-11-28
@description Spring Security의 보안설정 담당
*/

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public PasswordEncoder makePassword() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain myFilter(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .cors(cors -> cors.configurationSource(configurationSource()))
                .csrf(AbstractHttpConfigurer::disable) // csrf 비활성화 (RestFul, 프엔 따로 있어서)
                .httpBasic(AbstractHttpConfigurer::disable) // Basic 인증 비활성화 : Basic 인증은 사용자 이름 & 비밀번호를 Base64로 인코딩하여 인증값으로 활용
                .formLogin(FormLoginConfigurer::disable) // 기본 로그인 비활성화
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 방식 비활성화

                // request 인증, 인가 설정
                .authorizeHttpRequests(a -> a.requestMatchers(
                        "/oauth2/**", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html",
                                "api/snacks", "api/snacks/list")
                        .permitAll().
                        anyRequest().authenticated())

                // jwt 관련 설정
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class) // UsernamePasswordAuthenticationFilter : 이 클래스에서 폼 로그인 인증을 처리

                // oauth2 설정
                .oauth2Login(oauth ->
                        oauth.userInfoEndpoint(c -> c.userService(customOAuth2UserService))
                        .successHandler(oAuth2SuccessHandler))
                .build();
    }

    @Bean
    public CorsConfigurationSource configurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("*")); // 모든 HTTP 메서드 허용
        configuration.setAllowedHeaders(Arrays.asList("*")); // 모든 헤더값 허용
        configuration.setAllowCredentials(true); // 자격 증명 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // 모든 url 패턴에 대해서 cors 허용 설정
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
