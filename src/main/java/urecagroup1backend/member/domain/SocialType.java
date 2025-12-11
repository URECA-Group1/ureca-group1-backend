package urecagroup1backend.member.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.Arrays;

// 사용자가 어떤 소셜 로그인을 사용했는지, 소셜 로그인 자체의 식별 코드에 대한 정보 담고 있는 Enum

@RequiredArgsConstructor
@Getter
public enum SocialType {
    GOOGLE(null, "sub", "email"),
    KAKAO("kakao_account", "id", "email");

    private final String attributeKey; // 소셜에서 전달받은 데이터를 Parsing 하기 위한 Key 값
    private final String socialCode; // 각 소셜을 판별하는 판별 코드
    private final String identifier; // 소셜 로그인을 한 사용자의 정보를 불러올 때 필요한 Key 값

    // 어떤 소셜 로그인에 해당하는지 찾는 메서드
    public static SocialType from (String socialType) {
        String upperCastedSocialType = socialType.toUpperCase();

        return Arrays.stream(SocialType.values())
                .filter(item -> item.name().equals(upperCastedSocialType))
                .findFirst()
                .orElseThrow();
    }
}
