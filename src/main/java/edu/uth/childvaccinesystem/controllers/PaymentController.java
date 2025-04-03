package edu.uth.childvaccinesystem.controllers;

import edu.uth.childvaccinesystem.dtos.request.PaymentRequest;
import edu.uth.childvaccinesystem.entities.Payment;
import edu.uth.childvaccinesystem.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payment")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    // Lấy danh sách thanh toán
    @GetMapping
    public List<Payment> listPayments() {
        return paymentService.getAllPayments();
    }

    // Xử lý thanh toán (POST request)
    @PostMapping
    public String processPayment(@RequestBody PaymentRequest request) {
        return paymentService.savePayment(request);
    }

    // Cập nhật trạng thái thanh toán
    @PutMapping("/{id}/{status}")
    public String updatePayment(@PathVariable Long id, @PathVariable String status) {
        return paymentService.updatePaymentStatus(id, status);
    }

    // Xóa thanh toán
    @DeleteMapping("/{id}")
    public void deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
    }

    // API lấy tổng doanh thu
    // @GetMapping("/total")
    // public double getTotalRevenue() {
    //     return paymentService.getTotalRevenue();
    // }
}
