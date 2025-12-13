package urecagroup1backend.member.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/*
@file SecurityConfig.java
@author 신형서
@since 2025-11-28
@description 사용자가 어떤 소셜 로그인을 사용했는지, 소셜 로그인 자체의 식별 코드에 대한 정보 담고 있는 Enum
*/

@RequiredArgsConstructor
@Getter
public enum SocialType {
    GOOGLE,
    KAKAO
}
