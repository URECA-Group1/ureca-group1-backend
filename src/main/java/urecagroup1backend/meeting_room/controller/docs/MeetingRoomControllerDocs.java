package urecagroup1backend.meeting_room.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import urecagroup1backend.meeting_room.dto.MeetingRoomResponse;
import urecagroup1backend.meeting_room.dto.ReservationRequest;
import urecagroup1backend.meeting_room.dto.ReservationResponse;

import java.security.Principal;
import java.util.List;

@Tag(name = "회의실", description = "회의실 예약 관리 API")
public interface MeetingRoomControllerDocs {

    @Operation(
            summary = "잔여 회의실 조회",
            description = "예약 가능한 회의실 목록을 조회합니다. available=true이고 PENDING/ACTIVE 상태의 예약이 없는 회의실만 반환됩니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "잔여 회의실 조회 성공",
            content = @Content(
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "status": 200,
                                      "message": "잔여 회의실 조회 성공",
                                      "data": [
                                        {
                                          "id": 1,
                                          "available": true
                                        },
                                        {
                                          "id": 2,
                                          "available": true
                                        }
                                      ]
                                    }
                                    """
                    )
            )
    )
    urecagroup1backend.config.ApiResponse<List<MeetingRoomResponse>> getAvailableRooms();

    @Operation(
            summary = "예약 페이지 진입",
            description = "회의실 ID로 예약 페이지에 진입합니다. PENDING 상태의 예약이 생성되며, 인증된 사용자의 정보가 예약에 저장됩니다. 다른 사용자가 조회할 때 이미 예약된 것처럼 보입니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "예약 페이지 진입 성공",
            content = @Content(
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "status": 201,
                                      "message": "예약 페이지 진입 성공",
                                      "data": {
                                        "id": 1,
                                        "meetingRoomId": 1,
                                        "phoneNumber": null,
                                        "status": "PENDING"
                                      }
                                    }
                                    """
                    )
            )
    )
    urecagroup1backend.config.ApiResponse<ReservationResponse> enterReservationPage(
            @PathVariable Long meetingRoomId,
            @Parameter(hidden = true) Principal principal
    );

    @Operation(
            summary = "예약 완료",
            description = "예약 ID와 전화번호를 입력하여 예약을 완료합니다. PENDING 상태의 예약이 ACTIVE 상태로 변경됩니다. 인증된 사용자의 정보가 예약에 저장됩니다."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "예약 완료 요청 정보",
            required = true,
            content = @Content(
                    schema = @Schema(implementation = ReservationRequest.class),
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "phoneNumber": "010-1234-5678"
                                    }
                                    """
                    )
            )
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "예약 완료 성공",
            content = @Content(
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "status": 200,
                                      "message": "예약 완료",
                                      "data": {
                                        "id": 1,
                                        "meetingRoomId": 1,
                                        "phoneNumber": "010-1234-5678",
                                        "status": "ACTIVE"
                                      }
                                    }
                                    """
                    )
            )
    )
    urecagroup1backend.config.ApiResponse<ReservationResponse> completeReservation(
            @PathVariable Long reservationId,
            @RequestBody ReservationRequest request,
            @Parameter(hidden = true) Principal principal
    );

    @Operation(
            summary = "회의실 생성",
            description = "새로운 회의실을 생성합니다. 생성된 회의실은 자동으로 available=true 상태로 설정됩니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "회의실 생성 성공",
            content = @Content(
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "status": 201,
                                      "message": "회의실 생성 성공",
                                      "data": {
                                        "id": 1,
                                        "available": true
                                      }
                                    }
                                    """
                    )
            )
    )
    urecagroup1backend.config.ApiResponse<MeetingRoomResponse> createMeetingRoom();

    @Operation(
            summary = "사용자 예약 목록 조회",
            description = "로그인한 사용자의 회의실 예약 목록을 조회합니다. PENDING 또는 ACTIVE 상태의 예약만 반환됩니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "예약 목록 조회 성공",
            content = @Content(
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "status": 200,
                                      "message": "예약 목록 조회 성공",
                                      "data": [
                                        {
                                          "id": 1,
                                          "meetingRoomId": 1,
                                          "phoneNumber": "010-1234-5678",
                                          "status": "ACTIVE"
                                        },
                                        {
                                          "id": 2,
                                          "meetingRoomId": 2,
                                          "phoneNumber": null,
                                          "status": "PENDING"
                                        }
                                      ]
                                    }
                                    """
                    )
            )
    )
    urecagroup1backend.config.ApiResponse<List<ReservationResponse>> getUserReservations(
            @Parameter(hidden = true) Principal principal
    );

    @Operation(
            summary = "예약 취소",
            description = "예약 ID로 예약을 취소합니다. 본인의 예약만 취소할 수 있으며, 이미 취소된 예약은 취소할 수 없습니다. 취소 후 해당 회의실은 예약 가능 상태로 변경됩니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "예약 취소 성공",
            content = @Content(
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "status": 200,
                                      "message": "예약 취소 성공",
                                      "data": null
                                    }
                                    """
                    )
            )
    )
    urecagroup1backend.config.ApiResponse<Void> cancelReservation(
            @PathVariable Long reservationId,
            @Parameter(hidden = true) Principal principal
    );
}
