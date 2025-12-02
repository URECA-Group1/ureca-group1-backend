package urecagroup1backend.seat.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import urecagroup1backend.seat.domain.SeatReservation;
import urecagroup1backend.seat.dto.SeatReservationResponse;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatReservationRepository extends JpaRepository<SeatReservation, Long> {

    // 현재 좌석의 진행중인 예약 내역 확인
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<SeatReservation> findFirstBySeatIdAndIsDeleted(Long seatId, Boolean isDeleted);

    // 현재 좌석의 완료된 예약 내역 확인
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<SeatReservation> findAllBySeatIdAndIsDeleted(Long seatId, Boolean isDeleted);

    // 현재 유저의 좌석 예약 내역 (지난 것 포함)
    List<SeatReservation> findByUserId(Long userId);

    // 현재 활성화되어 있는 예약 내역 가져오기
    List<SeatReservation> findAllByIsDeleted(Boolean isDeleted);

    // 활성화된 내 예약 있는지 확인
    boolean existsByUserIdAndIsDeletedFalse(Long userId);

    Optional<SeatReservation> findByUserIdAndIsDeleted(Long userId, Boolean isDeleted);
}
