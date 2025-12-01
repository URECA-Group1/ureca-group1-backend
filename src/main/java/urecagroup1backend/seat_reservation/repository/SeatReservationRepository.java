package urecagroup1backend.seat_reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import urecagroup1backend.seat.domain.EntityStatus;
import urecagroup1backend.seat.domain.SeatEntry;
import urecagroup1backend.seat_reservation.domain.SeatReservation;

import java.util.Optional;

@Repository
public interface SeatReservationRepository extends JpaRepository<SeatReservation, Long> {
    Optional<SeatReservation> findBySeatIdAndStatus(Long seatId, EntityStatus entityStatus);
    Optional<SeatReservation> findByUserId(Long userId);
}
