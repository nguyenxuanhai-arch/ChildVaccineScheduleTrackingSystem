package edu.uth.childvaccinesystem.repositories;

import edu.uth.childvaccinesystem.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("SELECT p FROM Payment p JOIN p.appointment a JOIN a.child c WHERE c.parentUsername = :username")
    List<Payment> findByUsername(@Param("username") String username);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
           "FROM Payment p JOIN p.appointment a JOIN a.child c " +
           "WHERE p.id = :paymentId AND c.parentUsername = :username")
    boolean isPaymentBelongsToUser(@Param("paymentId") Long paymentId, @Param("username") String username);
}