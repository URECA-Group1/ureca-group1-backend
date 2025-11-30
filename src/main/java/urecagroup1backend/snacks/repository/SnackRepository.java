package urecagroup1backend.snacks.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface SnackRepository extends JpaRepository<Snack, Long> {

    //구매 (True -> False)
    @Modifying
    @Query("UPDATE Snack s SET s.status = false WHERE s.id = :id AND s.status = true")
    int purchaseSnack(@Param("id") Long id);

    // 재고 복구 (False -> True)
    @Modifying
    @Query("UPDATE Snack s SET s.status = true WHERE s.id = :id")
    void updateStatus(@Param("id") Long id);
}