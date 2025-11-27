package urecagroup1backend.snacks.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import urecagroup1backend.snacks.api.dto.SnackResponse;
import urecagroup1backend.snacks.application.service.SnacksService;

import java.util.List;

@RestController
@RequestMapping("/api/snacks")
public class SnacksController {

    private final SnacksService snacksService;

    public SnacksController(SnacksService snacksService) {
        this.snacksService = snacksService;
    }

    @GetMapping("/list")
    public ResponseEntity<List<SnackResponse>> getSnacks(){
        List<SnackResponse> snacks = snacksService.getAllSnacks();

        return ResponseEntity.ok(snacks);
    }
}
