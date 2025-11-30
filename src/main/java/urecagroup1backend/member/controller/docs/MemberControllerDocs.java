package urecagroup1backend.member.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import urecagroup1backend.oauth.dto.RedirectDto;

import java.util.Map;

@Tag(name = "회원", description = "회원 가입 및 OAuth 로그인 API")
public interface MemberControllerDocs {

    @Operation(
            summary = "구글 OAuth 로그인",
            description = "구글 인가 코드로 로그인하고 JWT 토큰을 발급합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "구글 로그인 성공",
            content = @Content(
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "status": 200,
                                      "message": "구글 로그인에 성공했습니다.",
                                      "data": {
                                        "id": 1,
                                        "token": "jwt-token-value"
                                      }
                                    }
                                    """
                    )
            )
    )
    urecagroup1backend.config.ApiResponse<?> googleLogin(
            @Schema(description = "구글 리디렉션 코드 정보", implementation = RedirectDto.class)
            RedirectDto redirectDto
    );

    @Operation(
            summary = "카카오 OAuth 로그인",
            description = "카카오 인가 코드로 로그인하고 JWT 토큰을 발급합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "카카오 로그인 성공",
            content = @Content(
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "status": 200,
                                      "message": "카카오 로그인에 성공했습니다.",
                                      "data": {
                                        "id": 1,
                                        "token": "jwt-token-value"
                                      }
                                    }
                                    """
                    )
            )
    )
    urecagroup1backend.config.ApiResponse<?> kakaoLogin(
            @Schema(description = "카카오 리디렉션 코드 정보", implementation = RedirectDto.class)
            RedirectDto redirectDto
    );
}
