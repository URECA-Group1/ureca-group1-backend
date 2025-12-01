package urecagroup1backend.seat_reservation.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import urecagroup1backend.common.domain.BaseTimeEntity;
import urecagroup1backend.seat.domain.EntityStatus;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
public class SeatReservation extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long seatId;
    private Long userId;

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
