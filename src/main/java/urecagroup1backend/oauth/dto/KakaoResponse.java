package urecagroup1backend.oauth.dto;

import urecagroup1backend.member.domain.SocialType;

import java.util.Map;

/*
@file GoogleResponse.java
@author 신형서
@since 2025-12-11
@description 카카오 로그인 했을 때 응답 값
*/

public class KakaoResponse implements OAuth2Response {
    private final Map<String, Object> attribute;
    private final Map<String, Object> kakaoAccount;
    private final Map<String, Object> profile;

    public KakaoResponse(Map<String, Object> attribute) {
        this.attribute = attribute;
        this.kakaoAccount = (Map<String, Object>) attribute.get("kakao_account");


        if(this.kakaoAccount != null) {
            this.profile = (Map<String, Object>)this.kakaoAccount.get("profile");
        }
        else {
            this.profile = null;
        }
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        return attribute.get("id").toString();
    }

    @Override
    public String getEmail() {
        if (kakaoAccount != null && kakaoAccount.containsKey("email")) {
            return kakaoAccount.get("email").toString();
        }
        return null; // 예외 처리 필요
    }

    @Override
    public String getName() {
        if (profile != null && profile.containsKey("nickname")) {
            return profile.get("nickname").toString();
        }
        return null; // 예외 처리 필요
    }

    @Override
    public String getProfile() {
        if (profile != null && profile.containsKey("profile_image_url")) {
            return profile.get("profile_image_url").toString();
        }
        return null; // 예외 처리 필요
    }

    @Override
    public SocialType getSocialType() {
        return SocialType.KAKAO;
    }
}
