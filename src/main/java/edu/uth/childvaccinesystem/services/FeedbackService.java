package edu.uth.childvaccinesystem.services;

import edu.uth.childvaccinesystem.entities.Feedback;
import edu.uth.childvaccinesystem.repositories.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAll();
    }

    public Optional<Feedback> getFeedbackById(Long id) {
        return feedbackRepository.findById(id);
    }

    public void saveFeedback(Feedback feedback) {
        feedback.setCreatedAt(LocalDateTime.now());
        feedbackRepository.save(feedback);
    }

    public void deleteFeedback(Long id) {
        feedbackRepository.deleteById(id);
    }

    public List<Feedback> getFeedbackByUserId(Long userId) {
        return feedbackRepository.findByUserId(userId);
    }
    public List<Feedback> getFeedbackBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return feedbackRepository.findByCreatedAtBetween(startDate, endDate);
    }

    public Feedback updateFeedback(Long id, Feedback updatedFeedback) {
        return feedbackRepository.findById(id).map(feedback -> {
            feedback.setContent(updatedFeedback.getContent());
            feedback.setUpdatedAt(LocalDateTime.now());
            return feedbackRepository.save(feedback);
        }).orElseThrow(() -> new RuntimeException("Feedback not found with id " + id));
    }
}