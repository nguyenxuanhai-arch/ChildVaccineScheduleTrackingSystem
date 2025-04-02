package edu.uth.childvaccinesystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import edu.uth.childvaccinesystem.entities.User;
import edu.uth.childvaccinesystem.entities.Feedback;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    boolean existsByUserAndMessage(User user, String message);
    Optional<Feedback> findByUser_Id(Long userId); // Sửa lại để truy vấn chính xác
}
