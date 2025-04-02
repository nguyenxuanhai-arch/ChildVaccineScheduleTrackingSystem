package edu.uth.childvaccinesystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import edu.uth.childvaccinesystem.entities.User;
import edu.uth.childvaccinesystem.entities.Feedback;
import java.util.Optional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    boolean existsByUserAndMessage(User user, String message);
    Optional<Feedback> findByUser_Id(Long userId); // Sửa lại để truy vấn chính xác

    List<Feedback> findByUserId(Long userId);

    List<Feedback> findByVaccineId(Long vaccineId);

    @Query("SELECT f FROM Feedback f WHERE LOWER(f.comments) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Feedback> findByCommentsContaining(@Param("keyword") String keyword);

    @Query("SELECT f FROM Feedback f WHERE f.createdDate > :date")
    List<Feedback> findByCreatedDateAfter(@Param("date") String date);

    List<Feedback> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
