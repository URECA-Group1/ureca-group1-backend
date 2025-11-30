package urecagroup1backend.payments.domain;

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
@Table(name ="payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;
/*
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user; // Member
*/
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "snack_id")
    private Snack snack;

    @Column(name = "payment_status")
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @CreatedDate
    @Column(name = "payment_time")
    private LocalDateTime createdAt;


    public enum PaymentStatus{
        PAID,       // 결제 완료
        REFUNDED    // 환불 완료
    }
}
