/*
@file PointController.java
@author 허영현
@version 1.0
@since 2025-12-02
@description 이 파일은 유저의 포인트 관리(잔여 포인트 조회, 포인트 충전, 포인트 사용 내역 조회) 컨트롤러입니다.
*/
package urecagroup1backend.points.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import urecagroup1backend.config.ApiResponse;
import urecagroup1backend.member.domain.CustomUserDetails;
import urecagroup1backend.points.controller.docs.PointControllerDocs;
import urecagroup1backend.points.dto.PointChargeRequest;
import urecagroup1backend.points.dto.PointResponse;
import urecagroup1backend.points.service.PointService;

@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
public class PointController implements PointControllerDocs {

    private final PointService pointService;

    @GetMapping
    @Override
    public ApiResponse<PointResponse> getPoints(@AuthenticationPrincipal CustomUserDetails user) {
        Long userId = user.getId();
        PointResponse response = pointService.getPoints(userId);
        return new ApiResponse<>(HttpStatus.OK, "포인트 조회 성공", response);
    }

    @PostMapping
    @Override
    public ApiResponse<PointResponse> chargePoints(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody @Valid PointChargeRequest request
    ) {
        Long userId = user.getId();
        PointResponse response = pointService.chargePoints(userId, request);
        return new ApiResponse<>(HttpStatus.OK, "포인트 충전 완료", response);
    }
}
