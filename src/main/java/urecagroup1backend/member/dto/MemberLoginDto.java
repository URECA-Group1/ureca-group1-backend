// 일반 로그인 안 써서 안 씀


package urecagroup1backend.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberLoginDto {
    private String email;
    private String password;
}

