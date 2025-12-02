package urecagroup1backend.member.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(nullable = false)
//    @NotNull
    private String password;

//    @Column(nullable = false)
//    @NotNull
    private String name;

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

    private String socialId;
}
