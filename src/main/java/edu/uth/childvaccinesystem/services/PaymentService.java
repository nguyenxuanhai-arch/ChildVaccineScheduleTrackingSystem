package edu.uth.childvaccinesystem.services;

import edu.uth.childvaccinesystem.dtos.request.PaymentRequest;
import edu.uth.childvaccinesystem.entities.Payment;
import edu.uth.childvaccinesystem.repositories.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    // Lấy danh sách thanh toán
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    // Lưu thanh toán từ request
    public String savePayment(PaymentRequest request) {
        // Kiểm tra xem thanh toán đã tồn tại chưa
        if (paymentRepository.existsById(request.getId())) {
            return "Payment already exists with this ID";
        }
        Payment payment = new Payment();
        payment.setAmount(request.getAmount());
        payment.setStatus(request.getStatus());
        payment.setCreatedAt(request.getPaymentDate() != null ? request.getPaymentDate() : LocalDateTime.now());
        paymentRepository.save(payment);
        return "Payment saved successfully";
    }

    // Cập nhật trạng thái thanh toán
    public String updatePaymentStatus(Long id, String status) {
        Optional<Payment> payment = paymentRepository.findById(id);
        if (payment.isPresent()) {
            Payment existingPayment = payment.get();
            existingPayment.setStatus(status);
            paymentRepository.save(existingPayment);
            return "Payment status updated successfully";
        }
        return "Payment not found";
    }

    // Xóa thanh toán
    public String deletePayment(Long id) {
        if (!paymentRepository.existsById(id)) {
            return "Payment not found";
        }
        paymentRepository.deleteById(id);
        return "Payment deleted successfully";
    }

//     // Tính tổng doanh thu
//     public double getTotalRevenue() {
//         // Logic để tính tổng doanh thu
//         Double revenue = paymentRepository.getTotalRevenue();// logic của bạn để lấy dữ liệu doanh thu
//         return (revenue != null) ? revenue : 0.0;
//     }
}
