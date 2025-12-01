package urecagroup1backend.common.domain;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

// 여러 엔티티에서 사용될 생성시간, 업데이트 시간
@MappedSuperclass // 모든 엔티티에서 이 클래스를 상속 받는 형식으로 (생성 시간, 업데이트 시간은 공통으로 필요한 것이라서)
@Getter
public class BaseTimeEntity {
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
