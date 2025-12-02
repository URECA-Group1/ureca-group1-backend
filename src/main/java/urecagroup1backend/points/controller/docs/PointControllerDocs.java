package urecagroup1backend.points.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import urecagroup1backend.config.ApiResponse;
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
    ApiResponse<PointResponse> getMyPoints();
}
