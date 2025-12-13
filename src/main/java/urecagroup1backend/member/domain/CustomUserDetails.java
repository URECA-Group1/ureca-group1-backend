package urecagroup1backend.member.domain;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import urecagroup1backend.member.dto.MemberDto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/*
@file CustomUserDetails.java
@author 신형서
@since 2025-11-28
@description 다른 로직에서 현재 로그인된 사용자의 정보가 필요할 때 사용
*/

@Builder
@Getter
public class CustomUserDetails implements OAuth2User, UserDetails {
    private final MemberDto memberDto;

    public CustomUserDetails(MemberDto memberDto) {
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
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return memberDto.getName();
    }

    public Long getId() {
        return memberDto.getId();
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
