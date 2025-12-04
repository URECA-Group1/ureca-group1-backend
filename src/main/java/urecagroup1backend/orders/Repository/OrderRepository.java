package urecagroup1backend.orders.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import urecagroup1backend.orders.domain.Order;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByMemberId(Long memberId);
}
