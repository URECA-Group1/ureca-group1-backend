package urecagroup1backend.oauth.domain;


// 안씀

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import urecagroup1backend.member.domain.SocialType;
import urecagroup1backend.member.dto.MemberDto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

// 안씀
public class CustomOAuth2User implements OAuth2User {
    private final MemberDto memberDto;

    public CustomOAuth2User(MemberDto memberDto) {
        this.memberDto = memberDto;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {

            @Override
            public String getAuthority() {
                return null; // 권한 분리 없음
            }
        });

        return collection;
    }

    @Override
    public String getName() {
        return memberDto.getName();
    }

    public String getEmail() {
        return memberDto.getEmail();
    }

    public String getProfile() {
        return memberDto.getProfileUrl();
    }

    public SocialType getSocialType() {
        return memberDto.getSocialType();
    }
}
