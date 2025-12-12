package urecagroup1backend.oauth.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 안씀

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties("ignoreUnknown = true") // 없는 필드는 자동 무시
public class GoogleProfileDto {
    private String sub;
    private String email;
    private String name;
    private String picture;

}
