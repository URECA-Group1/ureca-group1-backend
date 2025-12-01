package urecagroup1backend.seat.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import urecagroup1backend.member.domain.CustomUserDetails;
import urecagroup1backend.seat.dto.SeatCreationRequest;
import urecagroup1backend.seat.dto.SeatEntryResponse;
import urecagroup1backend.seat.dto.SeatResponse;

@Tag(name = "좌석", description = "좌석 입퇴실 및 예약 API")
public interface SeatControllerDocs {
    @Operation(
            summary = "좌석 입실",
            description = """
                    좌석 ID로 좌석에 입실합니다.
                    내부적으로 로그인된 사용자 ID를 받아 좌석에 입실 처리하며, 좌석 상태가 사용 중으로 변경됩니다.
                    """
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "좌석 입실 성공",
            content = @Content(
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "status": 200,
                                      "message": "좌석 입실 성공",
                                      "data": {
                                        "id": 3
                                        "seatId": 2,
                                        "entryTime": "2025-11-27T19:58:11.701458"
                                      }
                                    }
                                    """
                    )
            )
    )
    urecagroup1backend.config.ApiResponse<SeatEntryResponse> entry(
            @PathVariable Long seatId,
            @AuthenticationPrincipal CustomUserDetails user
    );

    @Operation(
            summary = "좌석 퇴실",
            description = """
                    좌석 ID로 좌석에서 퇴실합니다.
                    내부적으로 로그인된 사용자 ID를 받아 해당 사용자의 좌석 사용을 종료합니다.
                    """
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "좌석 퇴실 완료",
            content = @Content(
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "status": 200,
                                      "message": "좌석 퇴실 완료",
                                      "data": {
                                        "id": 3
                                        "seatId": 2,
                                        "entryTime": "2025-11-27T19:58:11.701458"
                                      }
                                    }
                                    """
                    )
            )
    )
    urecagroup1backend.config.ApiResponse<SeatEntryResponse> exitSeat(
            @PathVariable Long seatId,
            @AuthenticationPrincipal CustomUserDetails user
    );

    @Operation(
            summary = "좌석 생성",
            description = """
                    새로운 좌석을 생성합니다.
                    요청으로 받은 좌석 번호(이름)를 기반으로 좌석을 생성합니다.
                    """
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "좌석 생성 요청 정보",
            required = true,
            content = @Content(
                    schema = @Schema(implementation = SeatCreationRequest.class),
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "seatNumber": "A001"
                                    }
                                    """
                    )
            )
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "좌석 생성 성공",
            content = @Content(
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "status": 201,
                                      "message": "좌석 생성 성공",
                                      "data": {
                                        "id": 1,
                                        "seatNumber": "A001",
                                      }
                                    }
                                    """
                    )
            )
    )
    urecagroup1backend.config.ApiResponse<SeatResponse> createSeat(
            @RequestBody SeatCreationRequest request
    );
}
