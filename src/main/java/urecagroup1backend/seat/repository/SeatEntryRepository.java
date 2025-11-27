package urecagroup1backend.seat.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import urecagroup1backend.seat.domain.SeatEntry;

import java.util.Optional;

@Repository
public interface SeatEntryRepository extends JpaRepository<SeatEntry, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<SeatEntry> findBySeatId(Long seatId);
}
