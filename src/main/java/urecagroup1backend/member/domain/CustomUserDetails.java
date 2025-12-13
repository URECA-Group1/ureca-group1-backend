package urecagroup1backend.member.domain;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import urecagroup1backend.member.dto.MemberDto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
        return List.of(() -> "ROLE_USER");
    }

    @Override
    public String getPassword() {
        return null;
    }

    // 회원마다 구분되는 고유한 값 필요
    @Override
    public String getUsername() {
        return memberDto.getEmail();
    }

    public Long getId() {
        return memberDto.getId();
    }

    // 회원마다 구분되는 고유한 값 필요
    @Override
    public String getName() {
        return memberDto.getEmail();
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
