package urecagroup1backend.snacks.repository;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners   (AuditingEntityListener.class)
@Table(name ="snacks")
public class Snack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "snack_id")
    private Long id;

    @Column(name = "snack_name")
    private String Name;

    @Column(name = "snack_price")
    private int Price;

    @Column(name = "snack_status")
    private boolean Status;

}
