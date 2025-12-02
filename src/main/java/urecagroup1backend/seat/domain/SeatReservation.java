package urecagroup1backend.seat.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

// 좌석 예약 내역 도메인

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SeatReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 예약 ID

    private Long seatId; // 좌석 ID
    private Long userId; // 유저 ID

    @CreationTimestamp
    private LocalDateTime createdAt; // 생성 시각

    @UpdateTimestamp
    private LocalDateTime updatedAt; // 업데이트 시각

    @Builder.Default
    private Boolean isDeleted = false; // 삭제되었는지? (논리적 삭제)

    public void delete() { isDeleted = true; }
}
