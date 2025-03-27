package edu.uth.childvaccinesystem.controllers;

import edu.uth.childvaccinesystem.entities.Payment;
import edu.uth.childvaccinesystem.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/payment")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    // Hiển thị danh sách thanh toán
    @GetMapping
    public String listPayments(Model model) {
        List<Payment> payments = paymentService.getAllPayments();
        model.addAttribute("payments", payments);
        return "payment/list";
    }

    // Hiển thị form thanh toán
    @GetMapping("/checkout")
    public String checkoutForm(Model model) {
        model.addAttribute("payment", new Payment());
        return "payment/checkout";
    }

    // Xử lý thanh toán
    @PostMapping("/process")
    public String processPayment(@ModelAttribute Payment payment) {
        paymentService.savePayment(payment);
        return "redirect:/payment";
    }

    // Cập nhật trạng thái thanh toán
    @GetMapping("/update/{id}/{status}")
    public String updatePayment(@PathVariable Long id, @PathVariable String status) {
        paymentService.updatePaymentStatus(id, status);
        return "redirect:/payment";
    }

    // Xóa thanh toán
    @GetMapping("/delete/{id}")
    public String deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return "redirect:/payment";
    }

    // API lấy tổng doanh thu
    @GetMapping("/revenue")
    @ResponseBody
    public double getTotalRevenue() {
        return paymentService.getTotalRevenue();
    }
}
