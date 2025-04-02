package edu.uth.childvaccinesystem.services;

import edu.uth.childvaccinesystem.entities.Feedback;
import edu.uth.childvaccinesystem.entities.User;
import edu.uth.childvaccinesystem.repositories.FeedbackRepository;
import edu.uth.childvaccinesystem.repositories.UserRepository;  // Make sure to add this
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FeedbackService {
    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private UserRepository userRepository;  // Inject the UserRepository

    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAll();
    }

    public String saveFeedback(Feedback feedback) {
        // Check if the User exists
        if (feedback.getUser() == null) {
            return "User cannot be null";
        }

        // If the User ID is null, we need to check if the User exists, if not, save it
        Optional<User> userOptional = userRepository.findById(feedback.getUser().getId());
        if (userOptional.isEmpty()) {
            return "User does not exist in the database";
        }

        // Check for message validity
        if (feedback.getMessage() == null || feedback.getMessage().isEmpty()) {
            return "Feedback message cannot be empty";
        }

        // Check if the same feedback already exists for this user and message
        if (feedbackRepository.existsByUserAndMessage(feedback.getUser(), feedback.getMessage())) {
            return "Feedback already exists for this user and message";
        }

        // Save the feedback
        feedbackRepository.save(feedback);
        return "Feedback saved successfully";
    }

    public String deleteFeedback(Long id) {
        Optional<Feedback> existingFeedback = feedbackRepository.findById(id);
        if (existingFeedback.isPresent()) {
            feedbackRepository.deleteById(id);
            return "Feedback deleted successfully";
        } else {
            return "Feedback not found";
        }
    }
}
