package urecagroup1backend.common.lock;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @file DistributedLockExecutor
 * @author 최인호
 * @description 분산락 실행기 - 전략 패턴 + 템플릿 메서드 패턴
 *
 * 역할:
 * - DistributedLockOperation을 받아서 분산락을 적용하여 실행
 * - Lock { Transaction { 비즈니스 로직 } } 순서를 보장
 * - 재사용 가능한 분산락 실행 로직 제공
 */
@Service
@RequiredArgsConstructor
public class DistributedLockExecutor {

    private final DistributedLock distributedLock;

    /**
     * 분산락을 적용하여 Operation을 실행합니다.
     *
     * @param operation 실행할 분산락 작업
     * @param <T> 반환 타입
     * @return 비즈니스 로직 실행 결과
     */
    public <T> T execute(DistributedLockOperation<T> operation) {
        return distributedLock.executeWithLock(
            operation.getLockKey(),
            operation.getWaitTime(),
            operation.getLeaseTime(),
            operation::executeBusinessLogic
        );
    }
}