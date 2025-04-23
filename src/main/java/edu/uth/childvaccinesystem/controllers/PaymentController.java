package edu.uth.childvaccinesystem.controllers;

import edu.uth.childvaccinesystem.entities.Payment;
import edu.uth.childvaccinesystem.services.PaymentService;
import edu.uth.childvaccinesystem.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import edu.uth.childvaccinesystem.entities.Appointment;
import edu.uth.childvaccinesystem.services.AppointmentService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Controller
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/{appointmentId}")
    public String paymentPage(@PathVariable Long appointmentId, Model model) {
        Appointment appointment = appointmentService.getAppointmentById(appointmentId);
        double amount = calculateAmount(appointment);
        
        model.addAttribute("appointment", appointment);
        model.addAttribute("amount", amount);
        return "payments/payment";
    }
    
    @GetMapping("/confirm/{appointmentId}")
    public String paymentConfirmPage(@PathVariable Long appointmentId, Model model) {
        try {
            Appointment appointment = appointmentService.getAppointmentById(appointmentId);
            double amount = calculateAmount(appointment);
            
            model.addAttribute("appointment", appointment);
            model.addAttribute("amount", amount);
            return "payments/confirm";
        } catch (Exception e) {
            // Log the error
            System.err.println("Error showing payment confirmation page: " + e.getMessage());
            e.printStackTrace();
            
            // Redirect to appointments page with error message
            model.addAttribute("errorMessage", "Không thể tải thông tin lịch hẹn. Vui lòng thử lại sau.");
            return "redirect:/appointments";
        }
    }

    @PostMapping("/process-cash")
    @ResponseBody
    public Map<String, Object> processCashPayment(@RequestBody Map<String, Long> request) {
        Long appointmentId = request.get("appointmentId");
        Appointment appointment = appointmentService.getAppointmentById(appointmentId);
        
        Payment payment = new Payment(
            appointment,
            calculateAmount(appointment),
            "CASH",
            "COMPLETED",
            LocalDateTime.now(),
            "Thanh toán tiền mặt"
        );
        
        paymentService.savePayment(payment);
        
        return Map.of(
            "success", true,
            "message", "Thanh toán tiền mặt thành công"
        );
    }

    @PostMapping("/process-bank-transfer")
    @ResponseBody
    public Map<String, Object> processBankTransfer(@RequestBody Map<String, Long> request) {
        Long appointmentId = request.get("appointmentId");
        Appointment appointment = appointmentService.getAppointmentById(appointmentId);
        
        Payment payment = new Payment(
            appointment,
            calculateAmount(appointment),
            "BANK_TRANSFER",
            "PENDING",
            LocalDateTime.now(),
            "Chờ xác nhận chuyển khoản"
        );
        
        paymentService.savePayment(payment);
        
        return Map.of(
            "success", true,
            "message", "Đã ghi nhận thông tin chuyển khoản"
        );
    }

    @GetMapping("/history")
    public String paymentHistory(Model model) {
        String username = getCurrentUsername();
        List<Payment> payments = paymentService.getPaymentsByUsername(username);
        
        model.addAttribute("payments", payments);
        return "payments/history";
    }

    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createPayment(
            @RequestBody Map<String, Object> request,
            @RequestHeader("Authorization") String token) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Long appointmentId = Long.parseLong(request.get("appointmentId").toString());
            double amount = Double.parseDouble(request.get("amount").toString());
            String paymentMethod = request.get("paymentMethod").toString();
            String notes = request.get("notes") != null ? request.get("notes").toString() : null;

            if (amount <= 0) {
                response.put("success", false);
                response.put("message", "Số tiền thanh toán phải lớn hơn 0");
                return ResponseEntity.badRequest().body(response);
            }

            // Lấy thông tin appointment
            Appointment appointment = appointmentService.getAppointmentById(appointmentId);
            if (appointment == null) {
                response.put("success", false);
                response.put("message", "Không tìm thấy lịch hẹn");
                return ResponseEntity.badRequest().body(response);
            }

            // Tạo payment
            Payment payment = new Payment(
                appointment,
                amount,
                paymentMethod,
                "PENDING",
                LocalDateTime.now(),
                notes
            );

            // Lưu payment
            Payment savedPayment = paymentService.savePayment(payment);
            
            if (savedPayment != null) {
                response.put("success", true);
                response.put("payment", Map.of(
                    "id", savedPayment.getId(),
                    "amount", savedPayment.getAmount(),
                    "paymentMethod", savedPayment.getPaymentMethod(),
                    "status", savedPayment.getStatus(),
                    "notes", savedPayment.getNotes()
                ));
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

    @PostMapping("/confirm")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> confirmPayment(
            @RequestBody Map<String, Object> request,
            @RequestHeader("Authorization") String token) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Long paymentId = Long.parseLong(request.get("paymentId").toString());
            String status = request.get("status").toString();
            String notes = request.get("notes") != null ? request.get("notes").toString() : null;

            if (paymentId == null || status == null || status.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Thiếu thông tin bắt buộc");
                return ResponseEntity.badRequest().body(response);
            }

            Payment payment = paymentService.updatePaymentStatus(paymentId, status, notes);
            if (payment != null) {
                response.put("success", true);
                response.put("payment", Map.of(
                    "id", payment.getId(),
                    "amount", payment.getAmount(),
                    "paymentMethod", payment.getPaymentMethod(),
                    "status", payment.getStatus(),
                    "notes", payment.getNotes()
                ));
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

    private double calculateAmount(Appointment appointment) {
        if (appointment.getType() == Appointment.AppointmentType.VACCINE) {
            return appointment.getVaccine().getPrice();
        } else if (appointment.getType() == Appointment.AppointmentType.PACKAGE) {
            return appointment.getVaccinePackage().getPrice();
        }
        return 0.0;
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }
}
