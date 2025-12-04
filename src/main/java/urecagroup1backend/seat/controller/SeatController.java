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
import urecagroup1backend.seat.service.SeatFacadeService;
import urecagroup1backend.seat.service.SeatService;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/seats")
@RestController
public class SeatController implements SeatControllerDocs {
    private final SeatService seatService;
    private final SeatFacadeService seatFacadeService;

    // 전체 좌석 불러오기
    @GetMapping
    public ApiResponse<List<SeatResponse>> getAll() {
        List<SeatResponse> response = seatService.getAllSeats();
        return new ApiResponse<>(HttpStatus.OK, "전체 좌석 불러오기 성공", response);
    }

    // 예약
    @PostMapping("{seatId}/reservation")
    public ApiResponse<SeatReservationResponse> reservation(
            @PathVariable Long seatId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        // SeatReservationResponse response = seatService.reserveSeat(seatId, user.getId());

        // 분산 락 적용
        SeatReservationResponse response = seatFacadeService.tryReserveSeat(seatId, user.getId());

        return new ApiResponse<>(HttpStatus.OK, "좌석 예약 성공", response);
    }

    // 예약 취소
    @PostMapping("{seatId}/cancel")
    public ApiResponse<SeatReservationResponse> cancel(
            @PathVariable Long seatId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        SeatReservationResponse response = seatService.cancelReservation(seatId, user.getId());
        return new ApiResponse<>(HttpStatus.OK, "좌석 예약 취소 완료", response);
    }

    // 입실
    @PostMapping("{seatId}/entry")
    public ApiResponse<SeatResponse> enter(
            @PathVariable Long seatId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        // SeatResponse response = seatService.enterSeat(seatId, user.getId());

        // 분산 락 적용
        SeatResponse response = seatFacadeService.tryEnterSeat(seatId, user.getId());
        return new ApiResponse<>(HttpStatus.OK, "좌석 입실 성공", response);
    }

    // 퇴실
    @PostMapping("{seatId}/exit")
    public ApiResponse<SeatResponse> exit(
            @PathVariable Long seatId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        SeatResponse response = seatService.exitSeat(seatId, user.getId());
        return new ApiResponse<>(HttpStatus.OK, "좌석 퇴실 완료", response);
    }

    // 좌석 생성
    @PostMapping
    public ApiResponse<SeatResponse> createSeat(@RequestBody SeatCreationRequest request) {
        SeatResponse response = seatService.createSeat(request.toSeatNumber());
        return new ApiResponse<>(HttpStatus.CREATED, "좌석 생성 성공", response);
    }
}
