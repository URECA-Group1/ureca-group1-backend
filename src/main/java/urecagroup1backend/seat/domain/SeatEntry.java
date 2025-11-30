package urecagroup1backend.seat.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SeatEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long seatId;
    private Long userId;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Enumerated(value = EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(8)")
    @Builder.Default
    private EntityStatus status = EntityStatus.ACTIVE;

    public void active() {
        status = EntityStatus.ACTIVE;
    }

    public boolean isActive() {
        return status == EntityStatus.ACTIVE;
    }

    public void delete() {
        status = EntityStatus.DELETED;
    }

    public boolean isDeleted() {
        return status == EntityStatus.DELETED;
    }
}
