package urecagroup1backend.oauth.service;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Service;
import urecagroup1backend.member.domain.Member;
import urecagroup1backend.member.domain.SocialType;
import urecagroup1backend.member.repository.MemberRepository;
import urecagroup1backend.oauth.JwtTokenProvider;

import java.io.IOException;

@RequiredArgsConstructor
@Service
public class GoogleOAuth2LoginSuccess extends SimpleUrlAuthenticationSuccessHandler {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // oauth 프로필 추출
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String openId = oAuth2User.getAttribute("sub");
        String email = oAuth2User.getAttribute("email");

        // 회원가입 여부 확인
        Member member = memberRepository.findBySocialId(openId).orElse(null);
        if(member == null) {
            member = Member.builder()
                    .socialId(openId)
                    .email(email)
                    .socialType(SocialType.GOOGLE)
                    .build();

            memberRepository.save(member);
        }

        // jwt 토큰 생성
        String jwtToken = jwtTokenProvider.createToken(member.getEmail(), member.getId(), member.getName());

        // 클라이언트 redirect 방식으로 토큰 전달
        // response.sendRedirect("http://localhost:3000?token=" + jwtToken);

        Cookie jwtCookie = new Cookie("token", jwtToken);
        jwtCookie.setPath("/"); // 모든 경로에서 쿠키 사용 가능
        response.addCookie(jwtCookie);
        response.sendRedirect("https://urecastudycafe.store");
    }

}
