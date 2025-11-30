package urecagroup1backend.snacks.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import urecagroup1backend.snacks.dto.SnackRequest;
import urecagroup1backend.snacks.dto.SnackResponse;
import urecagroup1backend.snacks.repository.Snack;
import urecagroup1backend.snacks.repository.SnackRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SnacksService {

    private final SnackRepository snackRepository;

    public List<SnackResponse> getAllSnacks() {
    return snackRepository.findAll().stream()
            .map(SnackResponse::from)
            .collect(Collectors.toList());
    }
    @Transactional
    public SnackResponse createSnack(SnackRequest request) {
        Snack snack = Snack.builder()
                .name(request.name())
                .price(request.price())
                .status(request.status())
                .build();

        Snack savedSnack = snackRepository.save(snack);
        return SnackResponse.from(savedSnack);
    }
}
