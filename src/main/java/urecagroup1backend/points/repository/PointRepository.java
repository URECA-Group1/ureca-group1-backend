/*
@file FileName.java
@author 홍길동
@version 1.0
@since 2025-01-01
@description 이 파일은 ~ 기능을 수행하는 클래스입니다.
*/
package urecagroup1backend.points.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import urecagroup1backend.points.domain.Point;

import java.util.Optional;

public interface PointRepository extends JpaRepository<Point, Long> {
    // Member 엔티티의 id 기준으로 조회
    Optional<Point> findByMember_Id(Long userId);
}
