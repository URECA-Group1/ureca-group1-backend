package urecagroup1backend.orders.Reposiotry;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import urecagroup1backend.member.domain.Member;
import urecagroup1backend.snacks.repository.Snack;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "snack_orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "snack_id")
    private Snack snack;

    @Column(name = "order_status")
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @CreatedDate
    @Column(name = "order_time")
    private LocalDateTime createdAt;

    // 비즈니스 로직을 위한 상태 변경 메서드
    public void updateStatus(OrderStatus status) {
        this.status = status;
    }
    public enum OrderStatus {
        SUCCESS, // 주문성공, 결제 전
        FAIL,      // 실패
        PAID,       // 결제완료
        CANCELED   // 사용자 요청에 의한 취소
    }
}