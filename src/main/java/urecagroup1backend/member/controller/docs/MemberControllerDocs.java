package urecagroup1backend.member.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import urecagroup1backend.auth.dto.TokenResDto;
import urecagroup1backend.member.domain.CustomUserDetails;
import java.util.Map;

@Tag(name = "회원", description = "회원 정보 및 인증 관련 API")
public interface MemberControllerDocs {

    /**
     * GET /api/members/me
     */
    @Operation(
            summary = "내 로그인 정보 가져오기",
            description = "현재 로그인된 사용자의 ID, 이메일, 이름을 반환합니다.",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "내 로그인 정보 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"status\": \"OK\", \"message\": \"내 로그인 정보\", \"data\": {\"id\": 1, \"email\": \"user@example.com\", \"name\": \"홍길동\"}}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 (유효하지 않은 Access Token)",
                    content = @Content
            )
    })
    urecagroup1backend.config.ApiResponse<?> me(CustomUserDetails user);

    /**
     * POST /api/members/logout
     */
    @Operation(
            summary = "로그아웃",
            description = "서버의 Redis에 저장된 Refresh Token을 무효화하고, 클라이언트의 'access' 및 'refresh' 쿠키를 삭제합니다.",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "로그아웃 성공 및 쿠키 삭제",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"status\": \"OK\", \"message\": \"로그아웃 성공\", \"data\": null}"
                            ),
                            // Swagger에 응답 헤더(쿠키 삭제) 표시
                            schema = @Schema(implementation = Map.class)
                    ),
                    headers = {
                            @io.swagger.v3.oas.annotations.headers.Header(
                                    name = "Set-Cookie",
                                    description = "access=; Max-Age=0; ... (Access Token 쿠키 삭제)"
                            ),
                            @io.swagger.v3.oas.annotations.headers.Header(
                                    name = "Set-Cookie",
                                    description = "refresh=; Max-Age=0; ... (Refresh Token 쿠키 삭제)"
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 (유효하지 않은 Access Token)",
                    content = @Content
            )
    })
    urecagroup1backend.config.ApiResponse<?> logout(CustomUserDetails user, HttpServletResponse response);

    /**
     * POST /api/members/token/refresh
     */
    @Operation(
            summary = "Access Token 재발급",
            description = "만료된 Access Token을 갱신합니다. 요청 시 'refresh' 쿠키를 통해 Refresh Token을 전달해야 합니다.",
            security = @SecurityRequirement(name = "None")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "토큰 재발급 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"status\": \"OK\", \"message\": \"AccessToken & RefreshToken갱신 완료\", \"data\": null}"
                            )
                    ),
                    headers = {
                            @io.swagger.v3.oas.annotations.headers.Header(
                                    name = "Authorization",
                                    description = "새로운 Access Token (Bearer scheme)"
                            ),
                            @io.swagger.v3.oas.annotations.headers.Header(
                                    name = "Set-Cookie",
                                    description = "새로운 Refresh Token (HttpOnly 쿠키)"
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Refresh Token 만료 또는 유효하지 않음",
                    content = @Content
            )
    })
    @Parameter(
            in = ParameterIn.COOKIE,
            name = "refresh",
            description = "Refresh Token 쿠키",
            required = true,
            schema = @Schema(type = "string")
    )
    urecagroup1backend.config.ApiResponse<?> reissue(HttpServletRequest request, HttpServletResponse response);
}