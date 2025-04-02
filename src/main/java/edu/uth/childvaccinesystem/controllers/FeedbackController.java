package edu.uth.childvaccinesystem.controllers;

import edu.uth.childvaccinesystem.entities.Feedback;
import edu.uth.childvaccinesystem.services.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {
    @Autowired
    private FeedbackService feedbackService;

    @GetMapping
    public ResponseEntity<List<Feedback>> listFeedback() {
        List<Feedback> feedbackList = feedbackService.getAllFeedback();
        return ResponseEntity.ok(feedbackList);
    }

    @PostMapping 
    public ResponseEntity<String> saveFeedback(@RequestBody Feedback feedback) {
        String response = feedbackService.saveFeedback(feedback);
        if (response.equals("Feedback saved successfully")) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
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
}
