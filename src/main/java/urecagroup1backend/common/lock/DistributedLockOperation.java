package urecagroup1backend.common.lock;

/**
 * @file DistributedLockOperation
 * @author 최인호
 * @description 분산락 작업을 위한 템플릿 메서드 패턴 추상 클래스
 *
 * 템플릿 메서드 패턴:
 * - 알고리즘의 골격을 정의 (getLockKey, executeBusinessLogic)
 * - 서브클래스에서 구체적인 구현을 제공
 * - Lock { Transaction { 비즈니스 로직 } } 순서 보장
 */
public abstract class DistributedLockOperation<T> {

    /**
     * 락 키를 반환합니다. (필수 구현)
     * @return 분산락 키
     */
    protected abstract String getLockKey();

    /**
     * 실제 비즈니스 로직을 실행합니다. (필수 구현)
     * @Transactional이 적용된 메서드를 호출해야 합니다.
     * @return 비즈니스 로직 실행 결과
     */
    protected abstract T executeBusinessLogic();

    /**
     * 락 획득 대기 시간 (초)
     * 기본값: 5초 (오버라이드 가능)
     */
    protected long getWaitTime() {
        return 5L;
    }

    /**
     * 락 유지 시간 (초)
     * 기본값: 10초 (오버라이드 가능)
     */
    protected long getLeaseTime() {
        return 10L;
    }
}
