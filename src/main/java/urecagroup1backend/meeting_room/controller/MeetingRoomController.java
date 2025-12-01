package urecagroup1backend.meeting_room.controller;

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
    public ApiResponse<ReservationResponse> enterReservationPage(@PathVariable Long meetingRoomId) {
        ReservationResponse response = meetingRoomService.enterReservationPage(meetingRoomId);
        return new ApiResponse<>(HttpStatus.CREATED, "예약 페이지 진입 성공", response);
    }

    @Override
    @PostMapping("/reservations/{reservationId}/complete")
    public ApiResponse<ReservationResponse> completeReservation(
            @PathVariable Long reservationId,
            @RequestBody ReservationRequest request) {
        ReservationResponse response = meetingRoomService.completeReservation(reservationId, request.phoneNumber());
        return new ApiResponse<>(HttpStatus.OK, "예약 완료", response);
    }

    @Override
    @PostMapping
    public ApiResponse<MeetingRoomResponse> createMeetingRoom() {
        MeetingRoomResponse response = meetingRoomService.createMeetingRoom();
        return new ApiResponse<>(HttpStatus.CREATED, "회의실 생성 성공", response);
    }
}
