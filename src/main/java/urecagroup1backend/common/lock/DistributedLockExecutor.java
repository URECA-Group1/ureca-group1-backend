package urecagroup1backend.common.lock;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

/**
 * @file DistributedLockExecutor
 * @author 최인호
 * @description 분산락 실행기 - 람다 기반 간결한 분산락 적용
 *
 * 역할:
 * - 람다 표현식으로 분산락을 적용하여 실행
 * - Lock { Transaction { 비즈니스 로직 } } 순서를 보장
 * - 재사용 가능한 분산락 실행 로직 제공
 */
@Service
@RequiredArgsConstructor
public class DistributedLockExecutor {

    private final DistributedLock distributedLock;

    public <T> T executeWithLock(String lockKey, Supplier<T> operation) {
        return distributedLock.executeWithLock(lockKey, 5L, 10L, operation::get);
    }
}