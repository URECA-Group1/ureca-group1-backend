package urecagroup1backend.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


// 안 씀
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenReqDto {
    private String refreshToken;
}
