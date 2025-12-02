/*
@file FileName.java
@author 홍길동
@version 1.0
@since 2025-01-01
@description 이 파일은 ~ 기능을 수행하는 클래스입니다.
*/
package urecagroup1backend.points.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import urecagroup1backend.config.ApiResponse;
import urecagroup1backend.member.domain.CustomUserDetails;
import urecagroup1backend.points.controller.docs.PointControllerDocs;
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
}
