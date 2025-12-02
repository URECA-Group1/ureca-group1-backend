package urecagroup1backend.seat.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import urecagroup1backend.member.domain.CustomUserDetails;
import urecagroup1backend.seat.dto.SeatCreationRequest;
import urecagroup1backend.seat.dto.SeatReservationResponse;
import urecagroup1backend.seat.dto.SeatResponse;

import java.util.List;

@Tag(name = "Seat API", description = "좌석 관련 API")
public interface SeatControllerDocs {

    // 전체 좌석 조회
    @Operation(
            summary = "전체 좌석 조회",
            description = "모든 좌석의 정보를 불러옵니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "전체 좌석 불러오기 성공")
    })
    urecagroup1backend.config.ApiResponse<List<SeatResponse>> getAll();


    // 좌석 예약
    @Operation(
            summary = "좌석 예약",
            description = "특정 좌석을 사용자 계정으로 예약합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "좌석 예약 성공"),
            @ApiResponse(responseCode = "400", description = "좌석이 이미 예약됨"),
            @ApiResponse(responseCode = "404", description = "해당 좌석을 찾을 수 없음")
    })
    urecagroup1backend.config.ApiResponse<SeatReservationResponse> reservation(
            @Parameter(description = "예약하려는 좌석 ID") Long seatId,
            @Parameter(hidden = true) CustomUserDetails user
    );


    // 예약 취소
    @Operation(
            summary = "좌석 예약 취소",
            description = "사용자가 예약한 좌석을 취소합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "좌석 예약 취소 완료"),
            @ApiResponse(responseCode = "400", description = "해당 좌석은 예약되지 않았음"),
            @ApiResponse(responseCode = "404", description = "해당 좌석을 찾을 수 없음")
    })
    urecagroup1backend.config.ApiResponse<SeatReservationResponse> cancel(
            @Parameter(description = "취소할 좌석 ID") Long seatId,
            @Parameter(hidden = true) CustomUserDetails user
    );


    // 입실
    @Operation(
            summary = "좌석 입실",
            description = "예약한 좌석에 입실 처리합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "좌석 입실 성공"),
            @ApiResponse(responseCode = "400", description = "입실할 수 없는 상태(예약 없음, 이미 입실 등)"),
            @ApiResponse(responseCode = "404", description = "해당 좌석을 찾을 수 없음")
    })
    urecagroup1backend.config.ApiResponse<SeatResponse> enter(
            @Parameter(description = "입실할 좌석 ID") Long seatId,
            @Parameter(hidden = true) CustomUserDetails user
    );


    // 퇴실
    @Operation(
            summary = "좌석 퇴실",
            description = "현재 사용 중인 좌석을 퇴실 처리합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "좌석 퇴실 완료"),
            @ApiResponse(responseCode = "400", description = "퇴실할 수 없는 상태(입실 상태 아님 등)"),
            @ApiResponse(responseCode = "404", description = "해당 좌석을 찾을 수 없음")
    })
    urecagroup1backend.config.ApiResponse<SeatResponse> exit(
            @Parameter(description = "퇴실할 좌석 ID") Long seatId,
            @Parameter(hidden = true) CustomUserDetails user
    );


    // 좌석 생성
    @Operation(
            summary = "좌석 생성",
            description = "관리자가 새로운 좌석을 생성합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "좌석 생성 성공"),
            @ApiResponse(responseCode = "400", description = "이미 존재하는 좌석 번호")
    })
    urecagroup1backend.config.ApiResponse<SeatResponse> createSeat(
            @Parameter(description = "생성할 좌석 번호 요청 DTO") SeatCreationRequest request
    );
}
