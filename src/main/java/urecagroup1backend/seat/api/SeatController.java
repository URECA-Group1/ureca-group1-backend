package urecagroup1backend.seat.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import urecagroup1backend.config.ApiResponse;
import urecagroup1backend.seat.api.dto.SeatCreationRequest;
import urecagroup1backend.seat.api.dto.SeatEntryResponse;
import urecagroup1backend.seat.api.dto.SeatResponse;
import urecagroup1backend.seat.application.service.SeatService;

@RestController
@RequiredArgsConstructor
public class SeatController {
    private final SeatService seatService;

    @PostMapping("/api/seats/{seatId}/entry")
    public ApiResponse<SeatEntryResponse> entry(@PathVariable Long seatId) {
        // todo: userId 받아서 내려주기
        Long userId = 0L;
        SeatEntryResponse response = seatService.entry(seatId, userId);
        return new ApiResponse<>(HttpStatus.OK, "좌석 입실 성공", response);
    }

    @PostMapping("/api/seats/{seatId}/exit")
    public ApiResponse<SeatEntryResponse> exitSeat(@PathVariable Long seatId) {
        // todo: 로그인 유저의 userId 받아서 내려주기
        Long userId = 0L;
        SeatEntryResponse response = seatService.exitSeat(seatId, userId);
        return new ApiResponse<>(HttpStatus.OK, "좌석 퇴실 완료", response);
    }

    @PostMapping("/api/seats")
    public ApiResponse<SeatResponse> createSeat(@RequestBody SeatCreationRequest request) {
        SeatResponse response = seatService.createSeat(request.toSeatNumber());
        return new ApiResponse<>(HttpStatus.CREATED, "좌석 생성 성공", response);
    }
}
