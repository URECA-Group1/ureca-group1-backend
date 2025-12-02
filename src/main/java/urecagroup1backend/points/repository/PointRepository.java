package urecagroup1backend.points.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import urecagroup1backend.points.domain.Point;

public interface PointRepository extends JpaRepository<Point, Long> {
}
