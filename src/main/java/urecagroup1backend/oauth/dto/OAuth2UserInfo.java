package urecagroup1backend.oauth.dto;

import jakarta.security.auth.message.AuthException;
import lombok.Builder;
import urecagroup1backend.member.domain.Member;

import java.util.Map;

// 안씀

/*
@file OAuth2UserInfo.java
@author 신형서
@since 2025-12-11
@description registrationId (소셜 종류) 별로 유저 정보 생성
*/

@Builder
public record OAuth2UserInfo (
    String name,
    String email,
    String profile
) {
    public static OAuth2UserInfo of(String registrationId, Map<String, Object> attributes) {
        return switch(registrationId) {
            case "google" -> ofGoogle(attributes);
            case "kakao" -> ofKakao(attributes);
            default -> throw new IllegalArgumentException("지원되지 않는 OAuth2 로그인 ID입니다: " + registrationId);
        };
    }

    // 구글일 때
    private static OAuth2UserInfo ofGoogle(Map<String, Object> attributes) {
        return OAuth2UserInfo.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .profile((String) attributes.get("picture"))
                .build();
    }

    // 카카오일 때
    private static OAuth2UserInfo ofKakao(Map<String, Object> attributes) {
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) account.get("profile");

        return OAuth2UserInfo.builder()
                .name((String) profile.get("nickname"))
                .email((String) account.get("email"))
                .profile((String) profile.get("profile_image_url"))
                .build();
    }

    public Member toEntity() {
        return Member.builder()
                .name(name)
                .email(email)
                .profileImgUrl(profile)
                .build();
    }

}
