/*
@file Point.java
@author 허영현
@version 1.0
@since 2025-12-02
@description 유저의 포인트를 관리하는 도메인 입니다.
*/
package urecagroup1backend.points.domain;

import jakarta.persistence.*;
import lombok.*;
import urecagroup1backend.member.domain.Member;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "points")
public class Point {

    @Id
    @Column(name = "member_id")
    private Long id;  // PK = FK

    @MapsId  // Member PK를 그대로 사용
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    @Builder.Default
    private Long point = 0L;

    /// 포인트 충전
    public void addPoint(Long amount) {
        this.point += amount;
    }

    /// 포인트 사용
    public void usePoint(Long amount) {
        if (this.point < amount) {
            throw new IllegalArgumentException("포인트가 부족합니다.");
        }
        this.point -= amount;
    }
}
