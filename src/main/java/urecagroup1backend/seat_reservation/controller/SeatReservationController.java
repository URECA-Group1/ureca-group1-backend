package urecagroup1backend.seat_reservation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import urecagroup1backend.config.ApiResponse;
import urecagroup1backend.member.domain.CustomUserDetails;
import urecagroup1backend.seat_reservation.controller.docs.SeatReservationControllerDocs;
import urecagroup1backend.seat_reservation.dto.SeatReservationResDto;
import urecagroup1backend.seat_reservation.service.SeatReservationService;

@RequiredArgsConstructor
@RequestMapping("/api/seats")
@RestController
public class SeatReservationController implements SeatReservationControllerDocs {
    private final SeatReservationService seatReservationService;

    @PostMapping("/{seatId}/reserve")
    public ApiResponse<SeatReservationResDto> reserve(@AuthenticationPrincipal CustomUserDetails user,
                                                      @PathVariable Long seatId) {
        Long userId = user.getId();
        SeatReservationResDto response = seatReservationService.reserveSeat(seatId, userId);

        return new ApiResponse<>(HttpStatus.OK, "좌석 예약 성공", response);
    }

    @PostMapping("/{seatId}/cancel")
    public ApiResponse<SeatReservationResDto> cancel(@AuthenticationPrincipal CustomUserDetails user,
                                                      @PathVariable Long seatId) {
        Long userId = user.getId();
        SeatReservationResDto response = seatReservationService.cancelReservedSeat(seatId, userId);

        return new ApiResponse<>(HttpStatus.OK, "좌석 취소 성공", response);
    }
}
