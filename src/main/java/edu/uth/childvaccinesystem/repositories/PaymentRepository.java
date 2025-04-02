package edu.uth.childvaccinesystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import edu.uth.childvaccinesystem.entities.Payment;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByUserId(Long userId);

    List<Payment> findByStatus(String status);

    @Query("SELECT p FROM Payment p WHERE p.paymentDate BETWEEN :startDate AND :endDate")
    List<Payment> findByPaymentDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT p FROM Payment p WHERE p.amount > :amount")
    List<Payment> findByAmountGreaterThan(@Param("amount") Double amount);

    @Query("SELECT p FROM Payment p WHERE p.amount < :amount")
    List<Payment> findByAmountLessThan(@Param("amount") Double amount);

    List<Payment> findByUserIdAndStatus(Long userId, String status);
}