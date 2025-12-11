package urecagroup1backend.member.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import urecagroup1backend.common.domain.BaseTimeEntity;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 일반 로그인 안 써서 안 씀
//    @Column(nullable = false)
//    @NotNull
    private String password;

//    @Column(nullable = false)
//    @NotNull
    private String name;

    // google의 name, 카카오의 닉네임 모두 name으로 통일한다. (안 씀)
//    @Column(nullable = false)
//    @NotNull
    private String nickName;

    @Builder.Default
    private String profileImgUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSmYmaWrQ2kwplFb1FN1a07DmEKCAiIA4j31TfkvVr4STcOqQP7M-hITB3gPuckOSdRb8I&usqp=CAU";;

    @Column(nullable = false, unique = true)
    @NotNull
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    @NotNull
    private SocialType socialType;

    @Column(nullable = false, unique = true)
    @NotNull
    private String socialId;
}
