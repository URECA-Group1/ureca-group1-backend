/*
@file PointRepository.java
@author 허영현
@version 1.0
@since 2025-12-02
@description 유저의 포인트 레포지토리 입니다.
*/
package urecagroup1backend.points.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import urecagroup1backend.points.domain.Point;

import java.util.Optional;

public interface PointRepository extends JpaRepository<Point, Long> {
    // Member 엔티티의 id 기준으로 조회
    Optional<Point> findByMember_Id(Long userId);
}
