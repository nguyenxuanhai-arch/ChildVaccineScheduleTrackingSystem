package edu.uth.childvaccinesystem.services;

import edu.uth.childvaccinesystem.dtos.request.FeedbackRequest;
import edu.uth.childvaccinesystem.entities.Appointment;
import edu.uth.childvaccinesystem.entities.Feedback;
import edu.uth.childvaccinesystem.entities.User;
import edu.uth.childvaccinesystem.repositories.AppointmentRepository;
import edu.uth.childvaccinesystem.repositories.FeedbackRepository;
import edu.uth.childvaccinesystem.repositories.UserRepository; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private UserRepository userRepository;  // Inject the UserRepository

    @Autowired
    private AppointmentRepository appointmentRepository;

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
        // Check if the User exists
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
        
        // Kiểm tra xem đã đánh giá chưa
        Optional<Feedback> existingFeedback = feedbackRepository.findByAppointmentId(request.getAppointmentId());
        if (existingFeedback.isPresent()) {
            throw new RuntimeException("Lịch hẹn này đã được đánh giá");
        }
        
        // Lấy user từ appointment
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
