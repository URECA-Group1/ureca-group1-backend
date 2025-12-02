package urecagroup1backend.snacks.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface SnackRepository extends JpaRepository<Snack, Long> {

    // 구매: -1
    @Modifying
    @Query("UPDATE Snack s SET s.quantity = s.quantity - 1 WHERE s.id = :id AND s.quantity > 0")
    int purchaseSnack(@Param("id") Long id);

    // 재고 복구: +1
    @Modifying
    @Query("UPDATE Snack s SET s.quantity = s.quantity + 1 WHERE s.id = :id")
    void updateStatus(@Param("id") Long id);
}