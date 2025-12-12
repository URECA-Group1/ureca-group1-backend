package urecagroup1backend.seat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import urecagroup1backend.common.lock.DistributedLock;
import urecagroup1backend.seat.dto.SeatReservationResponse;
import urecagroup1backend.seat.dto.SeatResponse;

@Service
@RequiredArgsConstructor
public class SeatLockService {
    private final DistributedLock distributedLock; // 분산 락을 위해 구현된 객체
    private final SeatService seatService;

    public SeatReservationResponse tryReserveSeat(Long seatId, Long userId) {
        String lockKey = "seat:" + seatId;

        return distributedLock.executeWithLock(lockKey, 5, 10, () -> {
            // 여기서 호출되는 메서드는 다른 빈이라 @Transactional이 제대로 적용됨
            return seatService.reserveSeat(seatId, userId);
        });
    }

    // 입실 시도 (Redis 분산 Lock 적용)
    public SeatResponse tryEnterSeat(Long seatId, Long userId) {
        String lockKey = "seat:" + seatId;

        return distributedLock.executeWithLock(lockKey, 5, 10, () -> {
            // 여기서 호출되는 메서드는 다른 빈이라 @Transactional이 제대로 적용됨
            return seatService.enterSeat(seatId, userId);
        });
    }
}