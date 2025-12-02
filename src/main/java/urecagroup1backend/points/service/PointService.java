package urecagroup1backend.points.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import urecagroup1backend.member.domain.Member;
import urecagroup1backend.member.repository.MemberRepository;
import urecagroup1backend.points.domain.Point;
import urecagroup1backend.points.dto.PointResponse;
import urecagroup1backend.points.repository.PointRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 조회 전용 트랜잭션
public class PointService {

    private final PointRepository pointRepository;
    private final MemberRepository memberRepository;

    public PointResponse getPoints(Long userId) {
        // Member 먼저 조회
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보 없음"));

        // Point 조회, 없으면 새로 생성
        Point point = pointRepository.findByMember_Id(userId)
                .orElseGet(() -> {
                    Point newPoint = Point.builder()
                            .member(member)
                            .point(0L)
                            .build();
                    return pointRepository.save(newPoint);
                });

        return new PointResponse(point.getPoint());
    }
}
