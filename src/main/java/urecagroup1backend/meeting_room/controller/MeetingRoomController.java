package urecagroup1backend.meeting_room.controller;

import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import urecagroup1backend.config.ApiResponse;
import urecagroup1backend.meeting_room.controller.docs.MeetingRoomControllerDocs;
import urecagroup1backend.meeting_room.dto.MeetingRoomResponse;
import urecagroup1backend.meeting_room.dto.ReservationRequest;
import urecagroup1backend.meeting_room.dto.ReservationResponse;
import urecagroup1backend.meeting_room.service.MeetingRoomService;

/**
 * @file MeetingRoomController
 * @author 최인호
 * @description 미팅룸 컨트롤러
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/meeting-rooms")
public class MeetingRoomController implements MeetingRoomControllerDocs {

    private final MeetingRoomService meetingRoomService;

    @Override
    @GetMapping("/available")
    public ApiResponse<List<MeetingRoomResponse>> getAvailableRooms() {
        List<MeetingRoomResponse> rooms = meetingRoomService.getAvailableRooms();
        return new ApiResponse<>(HttpStatus.OK, "잔여 회의실 조회 성공", rooms);
    }

    @Override
    @PostMapping("/{meetingRoomId}/reservation-page")
    public ApiResponse<ReservationResponse> enterReservationPage(
            @PathVariable Long meetingRoomId,
            Principal principal) {
        String email = principal.getName();
        ReservationResponse response = meetingRoomService.enterReservationPage(meetingRoomId, email);
        return new ApiResponse<>(HttpStatus.CREATED, "예약 페이지 진입 성공", response);
    }

    @Override
    @PostMapping("/reservations/{reservationId}/complete")
    public ApiResponse<ReservationResponse> completeReservation(
            @PathVariable Long reservationId,
            @RequestBody ReservationRequest request,
            Principal principal) {
        String email = principal.getName();
        ReservationResponse response = meetingRoomService.completeReservation(reservationId, email, request.phoneNumber());
        return new ApiResponse<>(HttpStatus.OK, "예약 완료", response);
    }

    @Override
    @PostMapping
    public ApiResponse<MeetingRoomResponse> createMeetingRoom() {
        MeetingRoomResponse response = meetingRoomService.createMeetingRoom();
        return new ApiResponse<>(HttpStatus.CREATED, "회의실 생성 성공", response);
    }

    @Override
    @GetMapping("/reservations")
    public ApiResponse<List<ReservationResponse>> getUserReservations(Principal principal) {
        String email = principal.getName();
        List<ReservationResponse> reservations = meetingRoomService.getUserReservations(email);
        return new ApiResponse<>(HttpStatus.OK, "예약 목록 조회 성공", reservations);
    }

    @Override
    @DeleteMapping("/reservations/{reservationId}")
    public ApiResponse<Void> cancelReservation(
            @PathVariable Long reservationId,
            Principal principal) {
        String email = principal.getName();
        meetingRoomService.cancelReservation(reservationId, email);
        return new ApiResponse<>(HttpStatus.OK, "예약 취소 성공", null);
    }
}
