package urecagroup1backend.member.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import urecagroup1backend.member.domain.CustomUserDetails;

import java.util.Map;

/*
@file MemberController.java
@author 신형서
@since 2025-11-29
@description 회원 API Swagger 명세서
*/

@Tag(name = "회원 및 인증 (Member & Auth)", description = "회원 정보 조회, 토큰 기반 인증 및 로그아웃 관리")
public interface MemberControllerDocs {

    @Operation(
            summary = "로그인 사용자 정보 조회",
            description = "유효한 Access Token을 사용하여 현재 로그인된 사용자의 상세 정보를 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "조회 성공",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Map.class)) // 실제 응답 DTO로 대체 필요
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증 실패 (토큰 없음 또는 만료)",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class))
                    )
            }
    )
    @GetMapping("/me")
    urecagroup1backend.config.ApiResponse<?> me(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user
    );


    @Operation(
            summary = "로그아웃",
            description = "현재 Access Token으로 로그인된 사용자의 Refresh Token을 Redis에서 삭제하여 로그아웃 처리합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "로그아웃 성공",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증 실패 (토큰 없음)",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class))
                    )
            }
    )
    @PostMapping("/logout")
    urecagroup1backend.config.ApiResponse<?> logout(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user
    );


    @Operation(
            summary = "토큰 재발급 (Access/Refresh)",
            description = "만료된 Access Token을 갱신하고, Rolling Refresh Token 전략에 따라 Refresh Token도 갱신합니다. 새로운 Access Token은 응답 헤더(Authorization)로, Refresh Token은 쿠키로 반환됩니다.",
            parameters = {
                    @Parameter(
                            in = ParameterIn.HEADER,
                            name = "Authorization",
                            description = "유효한 Refresh Token (Bearer 접두사 포함)",
                            required = true,
                            schema = @Schema(type = "string", example = "Bearer eyJhbGciOiJ...")
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "토큰 갱신 성공",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class)),
                            headers = {
                                    @io.swagger.v3.oas.annotations.headers.Header(
                                            name = "Authorization",
                                            description = "새로 발급된 Access Token (Bearer)",
                                            schema = @Schema(type = "string")
                                    ),
                                    @io.swagger.v3.oas.annotations.headers.Header(
                                            name = "Set-Cookie",
                                            description = "새 Refresh Token (쿠키)",
                                            schema = @Schema(type = "string")
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Refresh Token 만료 또는 유효하지 않음",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class))
                    )
            }
    )
    @PostMapping("/token/refresh")
    urecagroup1backend.config.ApiResponse<?> reissue(
            @Parameter(hidden = true) HttpServletRequest request,
            @Parameter(hidden = true) HttpServletResponse response
    );
}