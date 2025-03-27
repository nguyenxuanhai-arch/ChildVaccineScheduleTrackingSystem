package edu.uth.childvaccinesystem.controllers;

import edu.uth.childvaccinesystem.entities.Feedback;
import edu.uth.childvaccinesystem.services.FeedbackService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/feedback")
public class FeedbackController {
    @Autowired
    private FeedbackService feedbackService;

    // Hiển thị danh sách feedback
    @GetMapping
    public String listFeedback(Model model) {
        List<Feedback> feedbackList = feedbackService.getAllFeedback();
        model.addAttribute("feedbackList", feedbackList);
        return "feedback/list";
    }

    // Hiển thị form thêm feedback
    @GetMapping("/add")
    public String addFeedbackForm(Model model) {
        model.addAttribute("feedback", new Feedback());
        return "feedback/add";
    }

    // Lưu feedback
    @PostMapping("/save")
    public String saveFeedback(@ModelAttribute Feedback feedback) {
        feedbackService.saveFeedback(feedback);
        return "redirect:/feedback";
    }

    // Xóa feedback
    @GetMapping("/delete/{id}")
    public String deleteFeedback(@PathVariable Long id) {
        feedbackService.deleteFeedback(id);
        return "redirect:/feedback";
    }
}
