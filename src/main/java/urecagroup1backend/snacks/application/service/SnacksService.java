package urecagroup1backend.snacks.application.service;

import org.springframework.stereotype.Service;
import urecagroup1backend.snacks.api.dto.SnackResponse;

import java.util.List;

@Service
public class SnacksService {
    public List<SnackResponse> getAllSnacks() {
    return List.of(
            new SnackResponse(1L,"초코송이",500,true)
            );
    }
}
