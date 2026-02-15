package edu.uth.childvaccinesystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import edu.uth.childvaccinesystem.entities.User;
import edu.uth.childvaccinesystem.entities.Feedback;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    boolean existsByUserAndMessage(User user, String message);

    @Query("SELECT f FROM Feedback f WHERE f.appointment.id = :appointmentId")
    Optional<Feedback> findByAppointmentId(@Param("appointmentId") Long appointmentId);
}
