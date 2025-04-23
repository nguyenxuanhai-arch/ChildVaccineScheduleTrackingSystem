package edu.uth.childvaccinesystem.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import edu.uth.childvaccinesystem.dtos.request.FeedbackRequest;
import edu.uth.childvaccinesystem.entities.Feedback;
import edu.uth.childvaccinesystem.services.FeedbackService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/feedback")
public class FeedbackController {
    @Autowired
    private FeedbackService feedbackService;

    @GetMapping
    public ResponseEntity<List<Feedback>> listFeedback() {
        List<Feedback> feedbackList = feedbackService.getAllFeedbacks();
        return ResponseEntity.ok(feedbackList);
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<?> submitFeedback(@RequestBody FeedbackRequest request) {
        try {
            Feedback feedback = feedbackService.saveFeedback(request);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Đánh giá của bạn đã được ghi nhận",
                "feedback", feedback
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFeedback(@PathVariable Long id) {
        String response = feedbackService.deleteFeedback(id);
        if (response.equals("Feedback deleted successfully")) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/appointment/{appointmentId}")
    @ResponseBody
    public ResponseEntity<?> getFeedbackByAppointment(@PathVariable Long appointmentId) {
        try {
            Feedback feedback = feedbackService.getFeedbackByAppointmentId(appointmentId);
            return ResponseEntity.ok(feedback);
        } catch (Exception e) {
            // Log lỗi để debug
            System.err.println("Error retrieving feedback for appointment " + appointmentId + ": " + e.getMessage());
            e.printStackTrace();
            
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/view/{appointmentId}")
    public String viewFeedbackDetails(@PathVariable Long appointmentId, Model model) {
        try {
            Feedback feedback = feedbackService.getFeedbackByAppointmentId(appointmentId);
            model.addAttribute("feedback", feedback);
            return "feedback/details"; // Template Thymeleaf mới cho xem đánh giá
        } catch (Exception e) {
            // Log lỗi để debug
            System.err.println("Error retrieving feedback for appointment " + appointmentId + ": " + e.getMessage());
            model.addAttribute("error", "Không tìm thấy đánh giá cho lịch hẹn này");
            return "redirect:/appointments/history";
        }
    }
}
