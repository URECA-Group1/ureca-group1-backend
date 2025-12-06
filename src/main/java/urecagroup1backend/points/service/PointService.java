/*
@file FileName.java
@author 홍길동
@version 1.0
@since 2025-01-01
@description 이 파일은 ~ 기능을 수행하는 클래스입니다.
*/
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
public class PointService {

    private final PointRepository pointRepository;
    private final MemberRepository memberRepository;

    // 잔여 포인트 조회
    @Transactional
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
