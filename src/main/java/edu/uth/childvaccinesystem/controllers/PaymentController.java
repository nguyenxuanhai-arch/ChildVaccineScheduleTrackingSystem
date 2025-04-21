package edu.uth.childvaccinesystem.controllers;

import edu.uth.childvaccinesystem.entities.Payment;
import edu.uth.childvaccinesystem.services.PaymentService;
import edu.uth.childvaccinesystem.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/history")
    public String paymentHistory(Authentication authentication, Model model) {
        String username = authentication.getName();
        List<Payment> payments = paymentService.getPaymentsByUsername(username);
        model.addAttribute("payments", payments);
        return "payment/history";
    }

    @GetMapping("/process/{paymentId}")
    public String processPayment(@PathVariable Long paymentId, Authentication authentication, Model model) {
        String username = authentication.getName();
        Payment payment = paymentService.getPaymentById(paymentId);
        
        if (payment == null) {
            return "redirect:/payment/history?error=not_found";
        }
        
        if (!paymentService.isPaymentBelongsToUser(paymentId, username)) {
            return "redirect:/payment/history?error=unauthorized";
        }

        model.addAttribute("payment", payment);
        return "payment/process";
    }

    @PostMapping("/process/{paymentId}")
    public String processPaymentSubmit(
            @PathVariable Long paymentId,
            @RequestParam String paymentMethod,
            @RequestParam(required = false) String notes,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        String username = authentication.getName();
        try {
            Payment payment = paymentService.updatePayment(paymentId, username, paymentMethod, notes);
            if (payment != null) {
                redirectAttributes.addFlashAttribute("success", "Cập nhật thông tin thanh toán thành công!");
                return "redirect:/payment/history";
            } else {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy thanh toán");
                return "redirect:/payment/process/" + paymentId;
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Cập nhật thất bại: " + e.getMessage());
            return "redirect:/payment/process/" + paymentId;
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createPayment(
            @RequestParam Long appointmentId,
            @RequestParam double amount,
            @RequestParam String paymentMethod,
            @RequestParam(required = false) String notes,
            @RequestHeader("Authorization") String token) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            if (amount <= 0) {
                response.put("success", false);
                response.put("message", "Số tiền thanh toán phải lớn hơn 0");
                return ResponseEntity.badRequest().body(response);
            }

            Payment payment = paymentService.createPayment(appointmentId, amount, paymentMethod, notes);
            
            if (payment != null) {
                response.put("success", true);
                response.put("payment", payment);
                response.put("message", "Tạo thanh toán thành công");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Không thể tạo thanh toán");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi tạo thanh toán: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/update-status")
    public ResponseEntity<Map<String, Object>> updatePaymentStatus(
            @RequestParam Long paymentId,
            @RequestParam String status,
            @RequestParam String transactionId) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            if (paymentId == null || status == null || status.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Thiếu thông tin bắt buộc");
                return ResponseEntity.badRequest().body(response);
            }

            Payment payment = paymentService.updatePaymentStatus(paymentId, status, transactionId);
            if (payment != null) {
                response.put("success", true);
                response.put("payment", payment);
                response.put("message", "Cập nhật trạng thái thanh toán thành công");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Không tìm thấy thanh toán");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi cập nhật trạng thái: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
