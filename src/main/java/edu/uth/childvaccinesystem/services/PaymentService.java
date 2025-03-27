package edu.uth.childvaccinesystem.services;

import edu.uth.childvaccinesystem.entities.Payment;
import edu.uth.childvaccinesystem.repositories.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public void savePayment(Payment payment) {
        payment.setStatus("PENDING"); // Mặc định khi thanh toán đang chờ xử lý
        paymentRepository.save(payment);
    }

    public void updatePaymentStatus(Long id, String status) {
        Payment payment = paymentRepository.findById(id).orElse(null);
        if (payment != null) {
            payment.setStatus(status);
            paymentRepository.save(payment);
        }
    }

    public void deletePayment(Long id) {
        paymentRepository.deleteById(id);
    }

    public double getTotalRevenue() {
        return paymentRepository.findAll().stream()
                .filter(p -> "COMPLETED".equals(p.getStatus()))
                .mapToDouble(Payment::getAmount)
                .sum();
    }
}
