package urecagroup1backend.member.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import urecagroup1backend.config.ApiResponse;
import urecagroup1backend.member.dto.MemberCreateDto;
import urecagroup1backend.member.dto.MemberLoginDto;
import urecagroup1backend.oauth.dto.RedirectDto;

import java.util.Map;

@Tag(name = "회원", description = "회원 가입 및 소셜 로그인 API")
public interface MemberControllerDocs {

    @Operation(
            summary = "일반 회원 가입",
            description = "회원 정보를 입력하여 일반 계정으로 회원 가입합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "회원 가입 성공",
            content = @Content(
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "status": 201,
                                      "message": "회원 가입 성공",
                                      "data": 1
                                    }
                                    """
                    )
            )
    )
    ApiResponse<?> memberCreate(
            @RequestBody(description = "회원 가입 정보", required = true,
                    content = @Content(schema = @Schema(implementation = MemberCreateDto.class)))
            MemberCreateDto memberCreateDto
    );

    @Operation(
            summary = "일반 회원 로그인",
            description = "이메일과 비밀번호로 로그인하고 JWT 토큰을 발급받습니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "로그인 성공",
            content = @Content(
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "status": 200,
                                      "message": "로그인 성공",
                                      "data": {
                                        "id": 1,
                                        "token": "jwt-token-value"
                                      }
                                    }
                                    """
                    )
            )
    )
    ApiResponse<Map<String, Object>> doLogin(
            @RequestBody(description = "로그인 정보", required = true,
                    content = @Content(schema = @Schema(implementation = MemberLoginDto.class)))
            MemberLoginDto memberLoginDto
    );

    @Operation(
            summary = "구글 OAuth 로그인",
            description = "구글 인가 코드로 로그인하고 JWT 토큰을 발급합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
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
    ApiResponse<Map<String, Object>> googleLogin(
            @RequestBody(description = "구글 리디렉션 코드", required = true,
                    content = @Content(schema = @Schema(implementation = RedirectDto.class)))
            RedirectDto redirectDto
    );

    @Operation(
            summary = "카카오 OAuth 로그인",
            description = "카카오 인가 코드로 로그인하고 JWT 토큰을 발급합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
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
    ApiResponse<Map<String, Object>> kakaoLogin(
            @RequestBody(description = "카카오 리디렉션 코드", required = true,
                    content = @Content(schema = @Schema(implementation = RedirectDto.class)))
            RedirectDto redirectDto
    );
}
