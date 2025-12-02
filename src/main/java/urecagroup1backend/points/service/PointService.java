package urecagroup1backend.points.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import urecagroup1backend.points.domain.Point;
import urecagroup1backend.points.dto.PointResponse;
import urecagroup1backend.points.repository.PointRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 조회 전용 트랜잭션
public class PointService {

    private final PointRepository pointRepository;

    public PointResponse getPoints(Long userId) {
        Point point = pointRepository.findByMember_Id(userId)
                .orElseThrow(() -> new IllegalArgumentException("포인트 정보 없음"));

        return new PointResponse(point.getPoint());
    }
}
