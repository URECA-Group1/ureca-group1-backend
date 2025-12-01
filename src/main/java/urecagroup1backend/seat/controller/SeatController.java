package urecagroup1backend.seat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import urecagroup1backend.config.ApiResponse;
import urecagroup1backend.member.domain.CustomUserDetails;
import urecagroup1backend.seat.controller.docs.SeatControllerDocs;
import urecagroup1backend.seat.dto.SeatCreationRequest;
import urecagroup1backend.seat.dto.SeatReservationResponse;
import urecagroup1backend.seat.dto.SeatResponse;
import urecagroup1backend.seat.service.SeatService;

@RestController
@RequiredArgsConstructor
public class SeatController implements SeatControllerDocs {
    private final SeatService seatService;

    @Override
    @PostMapping("/api/seats/{seatId}/entry")
    public ApiResponse<SeatReservationResponse> entry(
            @PathVariable Long seatId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        SeatReservationResponse response = seatService.entry(seatId, user.getId());
        return new ApiResponse<>(HttpStatus.OK, "좌석 입실 성공", response);
    }

    @Override
    @PostMapping("/api/seats/{seatId}/exit")
    public ApiResponse<SeatReservationResponse> exitSeat(
            @PathVariable Long seatId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        SeatReservationResponse response = seatService.exitSeat(seatId, user.getId());
        return new ApiResponse<>(HttpStatus.OK, "좌석 퇴실 완료", response);
    }

    @Override
    @PostMapping("/api/seats")
    public ApiResponse<SeatResponse> createSeat(@RequestBody SeatCreationRequest request) {
        SeatResponse response = seatService.createSeat(request.toSeatNumber());
        return new ApiResponse<>(HttpStatus.CREATED, "좌석 생성 성공", response);
    }
}
