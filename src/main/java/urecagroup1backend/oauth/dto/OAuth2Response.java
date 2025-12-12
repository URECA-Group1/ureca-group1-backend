package urecagroup1backend.oauth.dto;

import urecagroup1backend.member.domain.SocialType;

/*
@file OAuth2Response.java
@author 신형서
@since 2025-12-11
@description 로그인 응답값 공통 인터페이스
*/
public interface OAuth2Response {

    // 소셜 로그인 ID
    String getProviderId();

    // 이메일
    String getEmail();

    // 이름
    String getName();

    // 프로필
    String getProfile();

    // 소셜 타입
    SocialType getSocialType();
}
