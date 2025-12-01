package urecagroup1backend.seat_reservation.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import urecagroup1backend.member.domain.CustomUserDetails;

@Tag(name = "좌석 예약", description = "좌석 예약 및 예약 취소 API")
public interface SeatReservationControllerDocs {

    @Operation(
            summary = "좌석 예약",
            description = """
                    특정 좌석을 현재 로그인한 사용자가 예약합니다.
                    이미 예약된 좌석이거나, 사용자가 이미 예약을 보유한 경우 예약이 불가능합니다.
                    """
    )
    @ApiResponse(
            responseCode = "200",
            description = "좌석 예약 성공",
            content = @Content(
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "status": 200,
                                      "message": "좌석 예약 성공",
                                      "data": {
                                        "id": 5,
                                        "seatId": 2,
                                        "reservationTime": "2025-11-27T19:58:11.701458"
                                      }
                                    }
                                    """
                    )
            )
    )
    urecagroup1backend.config.ApiResponse<?> reserve(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long seatId
    );

    @Operation(
            summary = "좌석 예약 취소",
            description = """
                    특정 좌석에 대해 현재 로그인한 사용자의 예약을 취소합니다.
                    사용자가 해당 좌석을 예약한 적이 없으면 취소할 수 없습니다.
                    """
    )
    @ApiResponse(
            responseCode = "200",
            description = "좌석 예약 취소 성공",
            content = @Content(
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "status": 200,
                                      "message": "좌석 취소 성공",
                                      "data": {
                                        "id": 5,
                                        "seatId": 2,
                                        "reservationTime": "2025-11-27T19:58:11.701458"
                                      }
                                    }
                                    """
                    )
            )
    )
    urecagroup1backend.config.ApiResponse<?> cancel(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long seatId
    );
}
