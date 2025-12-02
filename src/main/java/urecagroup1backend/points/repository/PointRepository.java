package urecagroup1backend.points.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import urecagroup1backend.points.domain.Point;

import java.util.Optional;

public interface PointRepository extends JpaRepository<Point, Long> {
    Optional<Point> findByUserId(Long userId);
}
