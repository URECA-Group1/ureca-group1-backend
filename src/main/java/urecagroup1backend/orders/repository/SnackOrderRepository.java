package urecagroup1backend.orders.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import urecagroup1backend.orders.domain.SnackOrder;

public interface SnackOrderRepository extends JpaRepository<SnackOrder, Long> {
}
