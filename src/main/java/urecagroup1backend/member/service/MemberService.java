package urecagroup1backend.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import urecagroup1backend.member.domain.Member;
import urecagroup1backend.member.dto.MemberDto;
import urecagroup1backend.member.repository.MemberRepository;

import java.util.NoSuchElementException;

/*
@file MemberService.java
@author 신형서
@since 2025-11-28
@description 회원 서비스
*/

@RequiredArgsConstructor
@Service
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberDto getMemberInfo(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("해당 이메일로 등록된 사용자를 찾을 수 없습니다."));

        return MemberDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .profileUrl(member.getProfileImgUrl())
                .socialType(member.getSocialType())
                .build();
    }
}
