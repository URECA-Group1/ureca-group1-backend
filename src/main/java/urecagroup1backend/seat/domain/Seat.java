package urecagroup1backend.seat.domain;

import jakarta.persistence.*;
import lombok.*;

// 좌석 정보 도메인

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 좌석 ID

    @Column(length = 4)
    private String seatNumber; // 좌석 번호

    @Enumerated(value = EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(10)")
    @Builder.Default
    private SeatStatus seatStatus = SeatStatus.EMPTY; // 좌석 상태

    public void reserve() { seatStatus = SeatStatus.RESERVED; }
    public void use() { seatStatus = SeatStatus.USED; }
    public void empty() { seatStatus = SeatStatus.EMPTY; }
}
