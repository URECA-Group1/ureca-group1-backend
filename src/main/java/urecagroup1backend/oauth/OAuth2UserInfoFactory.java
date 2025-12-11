package urecagroup1backend.oauth;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import urecagroup1backend.member.domain.SocialType;

import java.util.Map;

public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(SocialType socialType, Map<String, Object> attributes) {
        switch (socialType) {
            case GOOGLE -> {
                return new GoogleOAuth2UserInfo(attributes);
            }

            case KAKAO -> {
                return new KakaoOAuth2UserInfo(attributes);
            }
        }

        throw new OAuth2AuthenticationException("INVALID SOCIAL TYPE");
    }
}
