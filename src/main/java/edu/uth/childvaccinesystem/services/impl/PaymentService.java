package edu.uth.childvaccinesystem.services.impl;

import edu.uth.childvaccinesystem.entities.Payment;
import edu.uth.childvaccinesystem.repositories.PaymentRepository;
import edu.uth.childvaccinesystem.repositories.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final AppointmentRepository appointmentRepository;

    public List<Payment> getPaymentsByUsername(String username) {
        return paymentRepository.findByUsername(username);
    }

    public Payment getPaymentById(Long paymentId) {
        try {
            return paymentRepository.findByIdWithAppointment(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        } catch (Exception e) {
            return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        }
    }

    public boolean isPaymentBelongsToUser(Long paymentId, String username) {
        return paymentRepository.isPaymentBelongsToUser(paymentId, username);
    }

    @Transactional
    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment updatePayment(Long paymentId, String username, String paymentMethod, String notes) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (!payment.getAppointment().getChild().getParentUsername().equals(username)) {
            throw new RuntimeException("Unauthorized access");
        }

        payment.setPaymentMethod(paymentMethod);
        payment.setNotes(notes);
        payment.setPaymentDate(LocalDateTime.now());

        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment createPayment(Long appointmentId, double amount, String paymentMethod, String notes) {
        var appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch hẹn"));

        Payment payment = Payment.builder()
                .appointment(appointment)
                .amount(amount)
                .paymentMethod(paymentMethod)
                .status("PENDING")
                .paymentDate(LocalDateTime.now())
                .notes(notes)
                .build();

        return paymentRepository.save(payment);
    }

    public Payment updatePaymentStatus(Long paymentId, String status, String transactionId) {
        Payment payment = getPaymentById(paymentId);
        if (payment == null) {
            throw new RuntimeException("Payment not found");
        }

        payment.setStatus(status);
        payment.setTransactionId(transactionId);
        return paymentRepository.save(payment);
    }

    public List<Payment> getAllPayments() {
        try {
            return paymentRepository.findAllWithDetails();
        } catch (Exception e) {
            System.err.println("Error loading payments with details: " + e.getMessage());
            e.printStackTrace();
            return paymentRepository.findAll(); // Fallback to basic query if detailed query fails
        }
    }
}
