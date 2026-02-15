package edu.uth.childvaccinesystem.services.impl;

import edu.uth.childvaccinesystem.dtos.request.FeedbackRequest;
import edu.uth.childvaccinesystem.entities.Appointment;
import edu.uth.childvaccinesystem.entities.Feedback;
import edu.uth.childvaccinesystem.entities.User;
import edu.uth.childvaccinesystem.repositories.AppointmentRepository;
import edu.uth.childvaccinesystem.repositories.FeedbackRepository;
import edu.uth.childvaccinesystem.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;

    public List<Feedback> getAllFeedbacks() {
        return feedbackRepository.findAll();
    }
    public List<Feedback> getAllFeedback() {
        return getAllFeedbacks();
    }
    public Feedback getFeedbackById(Long id) {
        return feedbackRepository.findById(id).orElse(null);
    }

    public String saveFeedback(Feedback feedback) {
        if (feedback.getUser() == null) {
            return "No user provided for feedback";
        }
        
        // Check for duplicate feedback
        if (feedbackRepository.existsByUserAndMessage(feedback.getUser(), feedback.getMessage())) {
            return "Duplicate feedback detected";
        }
        
        feedbackRepository.save(feedback);
        return "Feedback saved successfully";
    }

    public Feedback saveFeedback(FeedbackRequest request) {
        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
            .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch hẹn"));
        
        Optional<Feedback> existingFeedback = feedbackRepository.findByAppointmentId(request.getAppointmentId());
        if (existingFeedback.isPresent()) {
            throw new RuntimeException("Lịch hẹn này đã được đánh giá");
        }
        
        User user = appointment.getChild().getParent();
        
        Feedback feedback = new Feedback();
        feedback.setUser(user);
        feedback.setAppointment(appointment);
        feedback.setRating(request.getRating());
        feedback.setMessage(request.getMessage());
        feedback.setCreatedAt(LocalDateTime.now());
        
        return feedbackRepository.save(feedback);
    }

    public Feedback getFeedbackByAppointmentId(Long appointmentId) {
        return feedbackRepository.findByAppointmentId(appointmentId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy đánh giá cho lịch hẹn này"));
    }

    public String deleteFeedback(Long id) {
        if (!feedbackRepository.existsById(id)) {
            return "Feedback not found";
        }
        feedbackRepository.deleteById(id);
        return "Feedback deleted successfully";
    }
}
