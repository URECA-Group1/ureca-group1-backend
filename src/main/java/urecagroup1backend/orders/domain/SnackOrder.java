package urecagroup1backend.orders.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import urecagroup1backend.snacks.repository.Snack;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name ="snack_orders")
public class SnackOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;
/*
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;
*/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "snack_id")
    private Snack snack;

    @Column(name = "order_status")
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @CreatedDate
    @Column(name = "order_time")
    private LocalDateTime createdAt;


    public enum OrderStatus{
        COMPLETED,
        FAIL
    }
}
