package urecagroup1backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
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
import urecagroup1backend.oauth.JwtTokenFilter;
import urecagroup1backend.oauth.service.GoogleOAuth2LoginSuccess;
import urecagroup1backend.oauth.service.KakaoOAuth2LoginSuccess;

import java.util.Arrays;
import java.util.Map;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;
    private final GoogleOAuth2LoginSuccess googleOAuth2LoginSuccess;
    private final KakaoOAuth2LoginSuccess kakaoOAuth2LoginSuccess;

    @Bean
    public PasswordEncoder makePassword() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain myFilter(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .cors(cors -> cors.configurationSource(configurationSource()))
                .csrf(AbstractHttpConfigurer::disable) // csrf 비활성화 (RestFul, 프엔 따로 있어서)

                // Basic 인증 비활성화
                // Basic 인증은 사용자 이름 & 비밀번호를 Base64로 인코딩하여 인증값으로 활용
                .httpBasic(AbstractHttpConfigurer::disable)
                // 세션 방식을 비활성화
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 특정 url 패턴에 대해서는 인증처리 (Authentication 객체 생성) 제외
                .authorizeHttpRequests(a -> a.requestMatchers(
                                "/member/create", "/member/doLogin",
                                "/member/google/doLogin", "/member/kakao/doLogin", "/oauth2/**",
                                "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html",
                                "api/snacks", "api/snacks/list","api/orders/{snackId}",
                                "api/orders/{orderId}/payment", "api/orders/{orderId}/cancel",
                                "api/orders/list")
                        .permitAll().anyRequest().authenticated())
                // UsernamePasswordAuthenticationFilter : 이 클래스에서 폼 로그인 인증을 처리
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                // oauth 로그인이 성공했을 경우 실행할 클래스 정의
                .oauth2Login(o -> o.successHandler(oAuth2SuccessHandler()))
                .build();
    }

    // OAuth2SuccessHandler 빈 등록
    // Spring Security는 OAuth2 등록 ID(registrationId)에 따라 적절한 SuccessHandler를 찾도록 해야 함
    // 하지만 현재 SimpleUrlAuthenticationSuccessHandler를 사용 중이므로,
    // 모든 성공 핸들러를 아우르는 하나의 핸들러(Composite Handler)를 만들어야 함
    // 일단, Kakao와 Google을 모두 처리할 수 있는 하나의 SuccessHandler를 작성함

    @Bean
    public AuthenticationSuccessHandler oAuth2SuccessHandler() {
        return (request, response, authentication) -> {

            // 1. Authentication 객체를 OAuth2AuthenticationToken으로 캐스팅합니다.
            // OAuth2 인증 성공 시 authentication은 이 타입임을 확신할 수 있습니다.
            if (authentication instanceof OAuth2AuthenticationToken oAuth2Token) {

                // 2. 토큰에서 등록 ID (registrationId)를 안전하게 가져옵니다.
                // 이 방법이 oAuth2User.getAttributes()를 사용하는 것보다 훨씬 안전하고 표준적입니다.
                String registrationId = oAuth2Token.getAuthorizedClientRegistrationId();

                if ("google".equals(registrationId)) {
                    // Google 핸들러 실행
                    googleOAuth2LoginSuccess.onAuthenticationSuccess(request, response, authentication);
                } else if ("kakao".equals(registrationId)) {
                    // Kakao 핸들러 실행
                    kakaoOAuth2LoginSuccess.onAuthenticationSuccess(request, response, authentication);
                } else {
                    // 예상치 못한 소셜 타입 (선택 사항)
                    throw new IllegalStateException("Unsupported OAuth2 Provider: " + registrationId);
                }
            } else {
                // OAuth2가 아닌 다른 방식으로 인증이 성공한 경우 (예: 일반 ID/PW 로그인)
                // 필요한 경우 다른 Success Handler 로직을 여기에 추가할 수 있습니다.
                // 현재는 아무 작업도 하지 않거나 기본 리다이렉션을 수행하도록 둘 수 있습니다.
                response.sendRedirect("http://localhost:3000");
            }
        };
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
