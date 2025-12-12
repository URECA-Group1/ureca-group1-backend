package urecagroup1backend.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import urecagroup1backend.member.domain.SocialType;

/*
@file MemberDto.java
@author 신형서
@since 2025-11-28
@description 회원 Dto
*/

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {
    private Long id;
    private String name;
    private String email;
    private String profileUrl;
    private SocialType socialType;
}
