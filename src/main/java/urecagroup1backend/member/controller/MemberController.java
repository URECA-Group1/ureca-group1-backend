package urecagroup1backend.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import urecagroup1backend.config.ApiResponse;
import urecagroup1backend.member.controller.docs.MemberControllerDocs;
import urecagroup1backend.member.domain.CustomUserDetails;
import urecagroup1backend.member.domain.Member;
import urecagroup1backend.member.domain.SocialType;
import urecagroup1backend.member.dto.MemberCreateDto;
import urecagroup1backend.member.dto.MemberLoginDto;
import urecagroup1backend.member.dto.RefreshTokenReqDto;
import urecagroup1backend.member.service.MemberService;
import urecagroup1backend.oauth.JwtTokenProvider;
import urecagroup1backend.oauth.dto.AccessTokenDto;
import urecagroup1backend.oauth.dto.GoogleProfileDto;
import urecagroup1backend.oauth.dto.KakaoProfileDto;
import urecagroup1backend.oauth.dto.RedirectDto;
import urecagroup1backend.oauth.service.GoogleService;
import urecagroup1backend.oauth.service.KakaoService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("api/members")
public class MemberController {
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
//    private final GoogleService googleService;
//    private final KakaoService kakaoService;

    /* 일반 계정 회원 가입
    @PostMapping("/create")
    public ApiResponse<?> memberCreate(@RequestBody MemberCreateDto memberCreateDto) {
        Member member = memberService.create(memberCreateDto);

        return new ApiResponse<>(member.getId(), HttpStatus.CREATED);

    }
    */


    /* 일반 계정 로그인
    @PostMapping("/doLogin")
    public ApiResponse<?> doLogin(@RequestBody MemberLoginDto memberLoginDto) {
        // email, password 일치하는지 검증
        Member member = memberService.login(memberLoginDto);

        // 일치할 경우 jwt accessToken 생성
        String jwtToken = jwtTokenProvider.createToken(member.getEmail(), member.getName());

        Map<String, Object> loginInfo = new HashMap<>();
        loginInfo.put("id", member.getId());
        loginInfo.put("token", jwtToken);

        return new ApiResponse<>(loginInfo, HttpStatus.OK);

    }
     */


    /* 구글 로그인 (JWT 방식, Spring Security와 겹쳐서 주석처리)
    @PostMapping("/google/doLogin")
    public ApiResponse<?> googleLogin(@RequestBody RedirectDto redirectDto) {
        // accessToken 발급
        AccessTokenDto accessTokenDto = googleService.getAccessToken(redirectDto.getCode());

        // 사용자 정보 얻기
        GoogleProfileDto googleProfileDto = googleService.getGoogleProfile(accessTokenDto.getAccess_token());

        // 회원 가입 되어 있지 않다면 회원 가입
        Member originalMember = memberService.getMemberBySocialId(googleProfileDto.getSub());
        if(originalMember == null) {
            originalMember = memberService.createOauth(googleProfileDto.getSub(), googleProfileDto.getEmail(), SocialType.GOOGLE);
        }

        // 회원 가입 되어 있는 회원이면 토큰 발급
        String jwtToken = jwtTokenProvider.createToken(originalMember.getEmail(), originalMember.getId(), originalMember.getName());

        Map<String, Object> loginInfo = new HashMap<>();
        loginInfo.put("id", originalMember.getId());
        loginInfo.put("token", jwtToken);

        return new ApiResponse<>(HttpStatus.OK, "구글 로그인에 성공했습니다.", loginInfo);
    }
    */

    /* 카카오 로그인 (JWT 방식, Spring Security와 겹쳐서 주석처리)
    @PostMapping("/kakao/doLogin")
    public ApiResponse<?> kakaoLogin(@RequestBody RedirectDto redirectDto) {
        // accessToken 발급
        AccessTokenDto accessTokenDto = kakaoService.getAccessToken(redirectDto.getCode());

        // 사용자 정보 얻기
        KakaoProfileDto kakaoProfileDto = kakaoService.getKakaoProfile(accessTokenDto.getAccess_token());

        // 회원 가입 되어 있지 않다면 회원 가입
        Member originalMember = memberService.getMemberBySocialId(kakaoProfileDto.getId());
        if(originalMember == null) {
            originalMember = memberService.createOauth(kakaoProfileDto.getId(), kakaoProfileDto.getKakao_account().getEmail(), SocialType.KAKAO);
        }

        // 회원 가입 되어 있는 회원이면 토큰 발급
        String jwtToken = jwtTokenProvider.createToken(originalMember.getEmail(), originalMember.getId(), originalMember.getName());

        Map<String, Object> loginInfo = new HashMap<>();
        loginInfo.put("id", originalMember.getId());
        loginInfo.put("token", jwtToken);

        return new ApiResponse<>(HttpStatus.OK, "카카오 로그인에 성공했습니다.", loginInfo);
    }
     */

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
        // memberService.logout 로직 구현

        return new ApiResponse<>(HttpStatus.OK, "로그아웃 성공", null);
    }

    @PostMapping("/token/refresh")
    public ApiResponse<?> refreshToken(@RequestBody RefreshTokenReqDto refreshTokenReqDto) {
        String oldRefreshToken = refreshTokenReqDto.getRefreshToken();

        // AuthService 토큰 유효 확인 로직 구현

//
//        if(!jwtTokenProvider.validateToken(oldRefreshToken)) {
//            return new ApiResponse<>(HttpStatus.UNAUTHORIZED, "유효하지 않거나 만료된 Refresh Token입니다.", null);
//        }

        // null 수정 필요
        return new ApiResponse<>(HttpStatus.OK, "AccessToken 갱신 완료", null);

    }


}
