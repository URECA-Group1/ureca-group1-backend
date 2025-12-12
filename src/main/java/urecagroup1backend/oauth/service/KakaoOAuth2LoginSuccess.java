// 안씀

//package urecagroup1backend.oauth.service;
//
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
//import org.springframework.stereotype.Service;
//import urecagroup1backend.member.domain.Member;
//import urecagroup1backend.member.domain.SocialType;
//import urecagroup1backend.member.repository.MemberRepository;
//import urecagroup1backend.oauth.JwtTokenProvider;
//
//import java.io.IOException;
//import java.util.Map;
//
//@RequiredArgsConstructor
//@Service
//public class KakaoOAuth2LoginSuccess extends SimpleUrlAuthenticationSuccessHandler {
//
//    private final MemberRepository memberRepository;
//    private final JwtTokenProvider jwtTokenProvider;
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
//                                        Authentication authentication) throws IOException, ServletException {
//
//        // 1. oauth 프로필 추출
//        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
//
//        // Kakao의 고유 ID는 'id' 필드에 Long으로 들어와서 String으로 변경 필요
//        Long openIdLong = oAuth2User.getAttribute("id");
//        String openId = String.valueOf(openIdLong);
//
//        // 이메일은 'kakao_account' Map 안에 들어있음
//        Map<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");
//        String email = (kakaoAccount != null) ? (String) kakaoAccount.get("email") : null;
//
//        // 2. 회원가입 여부 확인
//        Member member = memberRepository.findBySocialId(openId).orElse(null);
//        if(member == null) {
//            member = Member.builder()
//                    .socialId(openId)
//                    .email(email)
//                    .socialType(SocialType.KAKAO) // SocialType만 GOOGLE -> KAKAO로 변경
//                    .build();
//
//            memberRepository.save(member);
//        }
//
//        // 3. jwt 토큰 생성 및 전달
//        String jwtToken = jwtTokenProvider.createToken(member.getEmail(), member.getId(), member.getName());
//
//        Cookie jwtCookie = new Cookie("token", jwtToken);
//        jwtCookie.setPath("/");
//        response.addCookie(jwtCookie);
//
//        // Next.js 클라이언트로 리다이렉트
//        response.sendRedirect("http://localhost:3000");
//    }
//}