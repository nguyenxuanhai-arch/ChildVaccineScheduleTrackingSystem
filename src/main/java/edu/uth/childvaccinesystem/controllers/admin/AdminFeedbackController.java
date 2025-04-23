package edu.uth.childvaccinesystem.controllers.admin;

import edu.uth.childvaccinesystem.entities.Feedback;
import edu.uth.childvaccinesystem.services.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

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
                model.addAttribute("feedback", feedback);
                model.addAttribute("appointment", feedback.getAppointment());
                model.addAttribute("user", feedback.getUser());
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
    public List<Feedback> getAllFeedbacksData() {
        try {
            return feedbackService.getAllFeedback();
        } catch (Exception e) {
            logger.error("Error getting feedback data: ", e);
            return new ArrayList<>();
        }
    }
} 