package urecagroup1backend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
@file TokenResDto.java
@author 신형서
@since 2025-12-11
@description 토큰 재발급 했을 때 컨트롤러로 전달할 때 사용하는 Dto
*/

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenResDto {
    private String accessToken;
    private String refreshToken;
}
