package urecagroup1backend.meeting_room.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class MeetingRoomReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_room_id", nullable = false)
    private MeetingRoom meetingRoom;

    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    public void completeReservation(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.status = ReservationStatus.ACTIVE;
    }

    public void cancel() {
        this.status = ReservationStatus.CANCELLED;
    }

    public enum ReservationStatus {
        PENDING,    // 예약 페이지 진입 (임시 예약)
        ACTIVE,     // 예약 완료
        CANCELLED   // 취소됨
    }
}
