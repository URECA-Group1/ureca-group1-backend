package urecagroup1backend.payments.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import urecagroup1backend.payments.domain.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
