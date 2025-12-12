package urecagroup1backend.oauth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 안씀
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RedirectDto {
    private String code;
}
