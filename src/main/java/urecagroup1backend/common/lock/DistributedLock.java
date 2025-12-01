package urecagroup1backend.common.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class DistributedLock {

    private final RedissonClient redissonClient;

    /**
     * 분산락을 획득하고 작업을 실행합니다.
     *
     * @param lockKey 락 키
     * @param waitTime 락 획득 대기 시간 (초)
     * @param leaseTime 락 유지 시간 (초)
     * @param task 실행할 작업
     * @param <T> 반환 타입
     * @return 작업 실행 결과
     */
    public <T> T executeWithLock(String lockKey, long waitTime, long leaseTime, LockTask<T> task) {
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean available = lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);

            if (!available) {
                log.warn("락 획득 실패: {}", lockKey);
                throw new IllegalStateException("다른 사용자가 처리 중입니다. 잠시 후 다시 시도해주세요.");
            }

            log.info("락 획득 성공: {}", lockKey);
            return task.execute();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("락 획득 중 인터럽트 발생", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("락 해제: {}", lockKey);
            }
        }
    }

    @FunctionalInterface
    public interface LockTask<T> {
        T execute();
    }
}
