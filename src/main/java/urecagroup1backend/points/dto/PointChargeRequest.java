/*
@file FileName.java
@author 홍길동
@version 1.0
@since 2025-01-01
@description 이 파일은 ~ 기능을 수행하는 클래스입니다.
*/
package urecagroup1backend.points.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PointChargeRequest {

    @NotNull
    @Min(value=1, message="충전 금액은 1 이상이어야 합니다.") // 최소 1포인트 이상 충전
    private Long amount;
}
