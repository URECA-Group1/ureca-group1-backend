package urecagroup1backend.auth.dto;

import urecagroup1backend.member.domain.SocialType;

import java.util.Map;

/*
@file GoogleResponse.java
@author 신형서
@since 2025-12-11
@description 구글 로그인 했을 때 응답 값
*/

public class GoogleResponse implements OAuth2Response {
    private final Map<String, Object> attribute;

    public GoogleResponse(Map<String, Object> attribute) {
        this.attribute = attribute;
    }

    @Override
    public String getProviderId() {
        return attribute.get("sub").toString();
    }

    @Override
    public String getEmail() {
        return attribute.get("email").toString();
    }

    @Override
    public String getName() {
        return attribute.get("name").toString();
    }

    @Override
    public String getProfile() {
        return attribute.get("picture").toString();
    }

    @Override
    public SocialType getSocialType() {
        return SocialType.GOOGLE;
    }
}
