package urecagroup1backend.seat.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import urecagroup1backend.seat.domain.SeatReservation;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatReservationRepository extends JpaRepository<SeatReservation, Long> {

    // 현재 좌석의 진행중인 예약 내역 확인
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<SeatReservation> findFirstBySeatIdAndStatus(Long seatId, Boolean isDeleted);

    // 현재 좌석의 완료된 예약 내역 확인
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<SeatReservation> findAllBySeatIdAndStatus(Long seatId, Boolean isDeleted);
}
