package edu.uth.childvaccinesystem.controllers.admin;

import edu.uth.childvaccinesystem.dtos.response.FeedbackResponse;
import edu.uth.childvaccinesystem.entities.Feedback;
import edu.uth.childvaccinesystem.services.impl.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/feedbacks")
public class AdminFeedbackController {

    private static final Logger logger = LoggerFactory.getLogger(AdminFeedbackController.class);

    @Autowired
    private FeedbackService feedbackService;

    @GetMapping("")
    public String feedbacks(Model model) {
        try {
            List<Feedback> feedbacks = feedbackService.getAllFeedback();
            model.addAttribute("feedbacks", feedbacks);
            
            // Add statistics
            double avgRating = feedbacks.stream()
                .mapToInt(Feedback::getRating)
                .average()
                .orElse(0);
            
            long fiveStarCount = feedbacks.stream()
                .filter(f -> f.getRating() == 5)
                .count();
            
            long fourStarCount = feedbacks.stream()
                .filter(f -> f.getRating() == 4)
                .count();
            
            long threeStarCount = feedbacks.stream()
                .filter(f -> f.getRating() == 3)
                .count();
            
            long twoStarCount = feedbacks.stream()
                .filter(f -> f.getRating() == 2)
                .count();
            
            long oneStarCount = feedbacks.stream()
                .filter(f -> f.getRating() == 1)
                .count();
            
            model.addAttribute("avgRating", String.format("%.1f", avgRating));
            model.addAttribute("fiveStarCount", fiveStarCount);
            model.addAttribute("fourStarCount", fourStarCount);
            model.addAttribute("threeStarCount", threeStarCount);
            model.addAttribute("twoStarCount", twoStarCount);
            model.addAttribute("oneStarCount", oneStarCount);
            model.addAttribute("totalFeedbacks", feedbacks.size());
            
        } catch (Exception e) {
            logger.error("Error loading feedbacks: ", e);
            model.addAttribute("error", "Cannot load feedback list: " + e.getMessage());
            model.addAttribute("feedbacks", List.of()); // Return empty list
        }
        return "admin/feedbacks/feedbacks";
    }
    
    @GetMapping("/view/{id}")
    public String getFeedbackDetails(@PathVariable Long id, Model model) {
        try {
            Feedback feedback = feedbackService.getFeedbackById(id);
            
            if (feedback != null) {
                FeedbackResponse feedbackResponse = new FeedbackResponse();
                feedbackResponse.setId(feedback.getId());
                if (feedback.getUser() != null) {
                    feedbackResponse.setUsername(feedback.getUser().getUsername());
                    feedbackResponse.setName(feedback.getUser().getName());
                }
                if (feedback.getAppointment() != null) {
                    feedbackResponse.setAppointmentId(feedback.getAppointment().getId());
                }
                feedbackResponse.setMessage(feedback.getMessage());
                feedbackResponse.setRating(feedback.getRating());
                feedbackResponse.setCreatedAt(feedback.getCreatedAt());
                
                model.addAttribute("feedback", feedbackResponse);
                return "admin/feedbacks/feedback-details";
            } else {
                // Handle case when feedback is not found
                model.addAttribute("error", "Feedback not found with ID: " + id);
                return "admin/feedbacks/feedbacks";
            }
        } catch (Exception e) {
            // Handle any other exceptions
            model.addAttribute("error", "Error loading feedback information: " + e.getMessage());
            return "admin/feedbacks/feedbacks";
        }
    }
    
    @PostMapping("/delete/{id}")
    public String deleteFeedback(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            String result = feedbackService.deleteFeedback(id);
            if (result.equals("Feedback deleted successfully")) {
                redirectAttributes.addFlashAttribute("message", "Feedback deleted successfully");
            } else {
                redirectAttributes.addFlashAttribute("error", result);
            }
        } catch (Exception e) {
            logger.error("Error deleting feedback: ", e);
            redirectAttributes.addFlashAttribute("error", "Cannot delete feedback: " + e.getMessage());
        }
        return "redirect:/admin/feedbacks";
    }
    
    @GetMapping("/data")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAllFeedbacksData() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Feedback> feedbacks = feedbackService.getAllFeedbacks();
            
            // Convert to DTOs
            List<FeedbackResponse> feedbackResponses = feedbacks.stream()
                .map(feedback -> {
                    FeedbackResponse dto = new FeedbackResponse();
                    dto.setId(feedback.getId());
                    if (feedback.getUser() != null) {
                        dto.setUsername(feedback.getUser().getUsername());
                        dto.setName(feedback.getUser().getName());
                    }
                    if (feedback.getAppointment() != null) {
                        dto.setAppointmentId(feedback.getAppointment().getId());
                    }
                    dto.setMessage(feedback.getMessage());
                    dto.setRating(feedback.getRating());
                    dto.setCreatedAt(feedback.getCreatedAt());
                    return dto;
                })
                .collect(Collectors.toList());
            
            response.put("data", feedbackResponses);
            response.put("recordsTotal", feedbackResponses.size());
            response.put("recordsFiltered", feedbackResponses.size());
            response.put("draw", 1);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error getting feedback data: ", e);
            response.put("data", new ArrayList<>());
            response.put("recordsTotal", 0);
            response.put("recordsFiltered", 0);
            response.put("draw", 1);
            response.put("error", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
} 