package urecagroup1backend.member.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import urecagroup1backend.common.domain.BaseTimeEntity;

/*
@file Member.java
@author 신형서
@since 2025-11-28
@description 회원 도메인
*/

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotNull
    private String name;

    @Builder.Default
    private String profileImgUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSmYmaWrQ2kwplFb1FN1a07DmEKCAiIA4j31TfkvVr4STcOqQP7M-hITB3gPuckOSdRb8I&usqp=CAU";;

    @Column(nullable = false, unique = true)
    @NotNull
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private SocialType socialType;

    @Column(nullable = false, unique = true)
    @NotNull
    private String socialId;
}
