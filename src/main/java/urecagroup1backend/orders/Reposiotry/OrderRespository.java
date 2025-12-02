package urecagroup1backend.orders.Reposiotry;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRespository extends JpaRepository<Order, Long> {
    List<Order> findAllByMemberId(Long memberId);
}
