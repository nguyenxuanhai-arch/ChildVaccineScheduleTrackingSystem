package edu.uth.childvaccinesystem.services;

import edu.uth.childvaccinesystem.entities.Payment;
import edu.uth.childvaccinesystem.entities.Appointment;
import edu.uth.childvaccinesystem.repositories.PaymentRepository;
import edu.uth.childvaccinesystem.repositories.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    public List<Payment> getPaymentsByUsername(String username) {
        return paymentRepository.findByUsername(username);
    }

    public Payment getPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId).orElse(null);
    }

    public boolean isPaymentBelongsToUser(Long paymentId, String username) {
        return paymentRepository.isPaymentBelongsToUser(paymentId, username);
    }

    @Transactional
    public Payment updatePayment(Long paymentId, String username, String paymentMethod, String notes) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thanh toán"));

        if (!isPaymentBelongsToUser(paymentId, username)) {
            throw new RuntimeException("Bạn không có quyền cập nhật thanh toán này");
        }

        if (!payment.getStatus().equals("PENDING")) {
            throw new RuntimeException("Chỉ có thể cập nhật thanh toán đang chờ xử lý");
        }

        payment.setPaymentMethod(paymentMethod);
        payment.setNotes(notes);
        payment.setStatus("COMPLETED");
        payment.setPaymentDate(LocalDateTime.now());

        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment createPayment(Long appointmentId, double amount, String paymentMethod, String notes) {
        var appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch hẹn"));

        Payment payment = new Payment(
            appointment,
            amount,
            paymentMethod,
            "PENDING",
            LocalDateTime.now(),
            notes
        );

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
}
