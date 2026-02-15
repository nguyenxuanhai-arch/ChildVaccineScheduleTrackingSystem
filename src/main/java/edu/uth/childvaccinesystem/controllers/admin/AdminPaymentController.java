package edu.uth.childvaccinesystem.controllers.admin;

import edu.uth.childvaccinesystem.entities.Appointment;
import edu.uth.childvaccinesystem.entities.Child;
import edu.uth.childvaccinesystem.entities.Payment;
import edu.uth.childvaccinesystem.services.impl.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/payments")
public class AdminPaymentController {

    private static final Logger logger = LoggerFactory.getLogger(AdminPaymentController.class);

    @Autowired
    private PaymentService paymentService;

    @GetMapping("")
    public String payments(Model model) {
        try {
            List<Payment> payments = paymentService.getAllPayments();
            logger.info("Loaded {} payments from database", payments.size());
            
            model.addAttribute("payments", payments);
        } catch (Exception e) {
            logger.error("Error loading payments: {}", e.getMessage(), e);
            model.addAttribute("error", "Cannot load payment data: " + e.getMessage());
            model.addAttribute("payments", List.of()); // Return empty list
        }
        return "admin/payments/payments";
    }

    @GetMapping("/{id}")
    public String viewPaymentDetails(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Payment payment = paymentService.getPaymentById(id);
            
            if (payment != null) {
                model.addAttribute("payment", payment);
                return "admin/payments/payment-details";
            } else {
                redirectAttributes.addFlashAttribute("error", "Payment not found with ID: " + id);
                return "redirect:/admin/payments";
            }
        } catch (Exception e) {
            logger.error("Error loading payment details: ", e);
            redirectAttributes.addFlashAttribute("error", "Error loading payment information: " + e.getMessage());
            return "redirect:/admin/payments";
        }
    }
    
    @PostMapping("/update-status")
    public String updatePaymentStatus(
            @RequestParam("paymentId") Long paymentId,
            @RequestParam("status") String status,
            @RequestParam(value = "notes", required = false) String additionalNotes,
            RedirectAttributes redirectAttributes) {
        try {
            if (paymentId == null) {
                redirectAttributes.addFlashAttribute("error", "Invalid payment ID");
                return "redirect:/admin/payments";
            }
            
            if (status == null || status.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Invalid payment status");
                return "redirect:/admin/payments";
            }
            
            // Get payment information
            Payment payment = paymentService.getPaymentById(paymentId);
            if (payment == null) {
                redirectAttributes.addFlashAttribute("error", "Payment not found with ID: " + paymentId);
                return "redirect:/admin/payments";
            }
            
            // Update information
            payment.setStatus(status);
            
            // Add notes
            String notes = "Status updated from [" + payment.getStatus() + "] to [" + status + "] at: " + LocalDateTime.now();
            if (additionalNotes != null && !additionalNotes.trim().isEmpty()) {
                notes += " - Notes: " + additionalNotes;
            }
            
            if (payment.getNotes() != null && !payment.getNotes().isEmpty()) {
                payment.setNotes(payment.getNotes() + " | " + notes);
            } else {
                payment.setNotes(notes);
            }
            
            // Save payment information
            paymentService.savePayment(payment);
            
            redirectAttributes.addFlashAttribute("message", "Payment status updated successfully");
        } catch (Exception e) {
            logger.error("Error updating payment status: ", e);
            redirectAttributes.addFlashAttribute("error", "Error updating payment status: " + e.getMessage());
        }
        
        return "redirect:/admin/payments";
    }

    @GetMapping("/ajax/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getPaymentAjax(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Payment payment = paymentService.getPaymentById(id);
            
            if (payment != null) {
                Map<String, Object> paymentData = new HashMap<>();
                paymentData.put("id", payment.getId());
                paymentData.put("amount", payment.getAmount());
                paymentData.put("status", payment.getStatus());
                paymentData.put("paymentMethod", payment.getPaymentMethod());
                paymentData.put("notes", payment.getNotes());
                
                if (payment.getPaymentDate() != null) {
                    paymentData.put("paymentDate", payment.getPaymentDate().toString());
                }
                
                // Include appointment info if available
                if (payment.getAppointment() != null) {
                    Map<String, Object> appointmentData = new HashMap<>();
                    Appointment appointment = payment.getAppointment();
                    
                    appointmentData.put("id", appointment.getId());
                    appointmentData.put("status", appointment.getStatus());
                    
                    if (appointment.getAppointmentDate() != null) {
                        appointmentData.put("date", appointment.getAppointmentDate().toString());
                    }
                    
                    appointmentData.put("time", appointment.getAppointmentTime());
                    appointmentData.put("type", appointment.getType());
                    
                    // Add child info if available
                    if (appointment.getChild() != null) {
                        Map<String, Object> childData = new HashMap<>();
                        Child child = appointment.getChild();
                        
                        childData.put("id", child.getId());
                        childData.put("name", child.getName());
                        
                        appointmentData.put("child", childData);
                    }
                    
                    // Add vaccine or package info
                    if ("VACCINE".equals(appointment.getType()) && appointment.getVaccine() != null) {
                        appointmentData.put("vaccineName", appointment.getVaccine().getName());
                    } else if ("PACKAGE".equals(appointment.getType()) && appointment.getVaccinePackage() != null) {
                        appointmentData.put("packageName", appointment.getVaccinePackage().getName());
                    }
                    
                    paymentData.put("appointment", appointmentData);
                }
                
                response.put("success", true);
                response.put("payment", paymentData);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Payment information not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            logger.error("Error getting payment details via AJAX: ", e);
            response.put("success", false);
            response.put("message", "Error loading payment information: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PostMapping("/{id}/status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updatePaymentStatus(@PathVariable Long id, @RequestParam String status) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (id == null) {
                response.put("success", false);
                response.put("message", "Invalid payment ID");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (status == null || status.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Invalid payment status");
                return ResponseEntity.badRequest().body(response);
            }
            
            Payment payment = paymentService.updatePaymentStatus(id, status, null);
            
            if (payment != null) {
                response.put("success", true);
                response.put("message", "Payment status has been updated to: " + status);
                response.put("paymentId", payment.getId());
                response.put("status", payment.getStatus());
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Payment not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            logger.error("Error updating payment status: ", e);
            response.put("success", false);
            response.put("message", "Error updating payment status: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
} 