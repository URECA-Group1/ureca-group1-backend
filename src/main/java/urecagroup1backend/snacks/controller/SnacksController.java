package urecagroup1backend.snacks.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import urecagroup1backend.config.ApiResponse;
import urecagroup1backend.snacks.controller.docs.SnackControllerDocs;
import urecagroup1backend.snacks.dto.SnackRequest;
import urecagroup1backend.snacks.dto.SnackResponse;
import urecagroup1backend.snacks.service.SnacksService;

import java.util.List;

@RestController
@RequestMapping("/api/snacks")
public class SnacksController implements SnackControllerDocs {

    private final SnacksService snacksService;

    public SnacksController(SnacksService snacksService) {
        this.snacksService = snacksService;
    }

    @Override
    @GetMapping("/list")
    public ApiResponse<List<SnackResponse>> getSnacks(){
        List<SnackResponse> snacks = snacksService.getAllSnacks();

        return new ApiResponse<>(HttpStatus.OK, "간식 목록 조회 성공",snacks);
    }


    @Override
    @PostMapping
    public ApiResponse<SnackResponse> createSnack(@RequestBody SnackRequest request) {
        SnackResponse response = snacksService.createSnack(request);
        return new ApiResponse<>(HttpStatus.CREATED, "간식 생성 완료", response);
    }
}
