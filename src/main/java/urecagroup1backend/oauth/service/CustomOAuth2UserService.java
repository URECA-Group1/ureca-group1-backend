package urecagroup1backend.oauth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import urecagroup1backend.member.domain.CustomUserDetails;
import urecagroup1backend.member.domain.Member;
import urecagroup1backend.member.dto.MemberDto;
import urecagroup1backend.member.repository.MemberRepository;
import urecagroup1backend.oauth.domain.CustomOAuth2User;
import urecagroup1backend.oauth.dto.*;

import java.util.Map;
import java.util.Optional;

/*
@file CustomOAuth2UserService.java
@author 신형서
@since 2025-12-11
@description SecurityConfig에서 로그인 성공 이후 사용자 정보 가져오는 클래스
*/

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info(">>>> CustomOAuth2UserService loadUser 메서드 진입");
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // System.out.println(oAuth2User);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;

        if (registrationId.equals("google")) {
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        }
        else if (registrationId.equals("kakao")) {
            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        }
        else {
            return null;
        }

        String socialId = oAuth2Response.getProviderId();

        Optional<Member> originMember = memberRepository.findBySocialId(socialId);

        Member saved = null;
        // 기존에 가입된 적 없으면
        if(originMember.isEmpty()) {
            Member newMember = Member.builder()
                    .name(oAuth2Response.getName())
                    .email(oAuth2Response.getEmail())
                    .profileImgUrl(oAuth2Response.getProfile())
                    .socialType(oAuth2Response.getSocialType())
                    .socialId(socialId)
                    .build();

            log.info("[log] name: " + newMember.getName());
            log.info("[log] email: " + newMember.getEmail());
            log.info("[log] profileImgUrl: " + newMember.getProfileImgUrl());
            log.info("[log] socialType: " + newMember.getSocialType());
            log.info("[log] socialId: " + newMember.getSocialId());

            saved = memberRepository.save(newMember);


        }

        else {
            saved = originMember.get();
        }

        MemberDto memberDto = MemberDto.builder()
                .id(saved.getId())
                .name(saved.getName())
                .email(saved.getEmail())
                .profileUrl(saved.getProfileImgUrl())
                .socialType(saved.getSocialType())
                .build();

        return new CustomUserDetails(memberDto);
    }
}
