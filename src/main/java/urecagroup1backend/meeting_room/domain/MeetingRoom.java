package urecagroup1backend.meeting_room.domain;

import jakarta.persistence.*;
import lombok.*;

/**
 * @file MeetingRoom
 * @author 최인호
 * @description 미팅룸 테이블
 */

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MeetingRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Boolean available;

    public void updateAvailability(Boolean available) {
        this.available = available;
    }
}
