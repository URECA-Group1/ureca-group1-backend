package urecagroup1backend.snacks.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface SnackRepository extends JpaRepository<Snack, Long> {

    // 구매: 재고 1개 -> 0개로 변경 (낙관적 락 역할)
    // 조건: id가 일치하고, 현재 재고(status)가 1인 경우에만 0으로 업데이트
    @Modifying
    @Query("UPDATE Snack s SET s.status = 0 WHERE s.id = :id AND s.status = 1")
    int purchaseSnack(@Param("id") Long id);

    // 재고 복구: 재고 0개 -> 1개로 변경
    @Modifying
    @Query("UPDATE Snack s SET s.status = 1 WHERE s.id = :id")
    void updateStatus(@Param("id") Long id);
}