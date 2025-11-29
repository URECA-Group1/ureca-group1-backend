package urecagroup1backend.snacks.api.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;
import urecagroup1backend.snacks.dto.SnackRequest;
import urecagroup1backend.snacks.dto.SnackResponse;

import java.util.List;

@Tag(name = "간식", description = "간식 조회 및 등록 API")
public interface SnackControllerDocs {

    @Operation(
            summary = "간식 목록 조회",
            description = "등록된 모든 간식의 목록을 조회합니다. 판매 중(status=true)인 간식과 품절된 간식이 모두 포함될 수 있습니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "간식 목록 조회 성공",
            content = @Content(
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "status": 200,
                                      "message": "간식 목록 조회 성공",
                                      "data": [
                                        {
                                          "id": 1,
                                          "name": "초코송이",
                                          "price": 500,
                                          "status": true
                                        },
                                        {
                                          "id": 2,
                                          "name": "빈츠",
                                          "price": 1000,
                                          "status": true
                                        },
                                        {
                                          "id": 3,
                                          "name": "포카칩",
                                          "price": 1500,
                                          "status": false
                                        }
                                      ]
                                    }
                                    """
                    )
            )
    )
    urecagroup1backend.config.ApiResponse<List<SnackResponse>> getSnacks();

    @Operation(
            summary = "간식 등록",
            description = "새로운 간식을 시스템에 등록합니다. 초기 등록 시 status는 true(판매중)로 설정하는 것을 권장합니다."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "등록할 간식 정보",
            required = true,
            content = @Content(
                    schema = @Schema(implementation = SnackRequest.class),
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "name": "새우깡",
                                      "price": 1200,
                                      "status": true
                                    }
                                    """
                    )
            )
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "간식 등록 성공",
            content = @Content(
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "status": 201,
                                      "message": "간식 생성 완료",
                                      "data": {
                                        "id": 4,
                                        "name": "새우깡",
                                        "price": 1200,
                                        "status": true
                                      }
                                    }
                                    """
                    )
            )
    )
    urecagroup1backend.config.ApiResponse<SnackResponse> createSnack(
            @RequestBody SnackRequest request
    );
}