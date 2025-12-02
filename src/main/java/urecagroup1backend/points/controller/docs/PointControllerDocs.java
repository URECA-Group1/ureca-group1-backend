/*
@file FileName.java
@author 홍길동
@version 1.0
@since 2025-01-01
@description 이 파일은 ~ 기능을 수행하는 클래스입니다.
*/
package urecagroup1backend.points.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import urecagroup1backend.config.ApiResponse;
import urecagroup1backend.member.domain.CustomUserDetails;
import urecagroup1backend.points.dto.PointResponse;

@Tag(name = "포인트", description = "포인트 관리 API")
public interface PointControllerDocs {

    @Operation(
            summary = "잔여 포인트 조회",
            description = "현재 로그인한 사용자의 잔여 포인트를 조회합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "포인트 조회 성공",
            content = @Content(
                    examples = @ExampleObject(
                            value = """
                                   {
                                     "status": 200,
                                     "message": "포인트 조회 성공",
                                     "data": {
                                       "points": 5000
                                     }
                                   }
                                   """
                    )
            )
    )
    ApiResponse<PointResponse> getPoints(@AuthenticationPrincipal CustomUserDetails user);
}
