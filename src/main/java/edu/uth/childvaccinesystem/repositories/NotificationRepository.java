package edu.uth.childvaccinesystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import edu.uth.childvaccinesystem.entities.Notification;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserId(Long userId);

    List<Notification> findByUserIdAndIsReadFalse(Long userId);

    @Query("SELECT n FROM Notification n WHERE n.createdDate > :date")
    List<Notification> findByCreatedDateAfter(@Param("date") LocalDateTime date);

    @Query("SELECT n FROM Notification n WHERE LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Notification> findByTitleContaining(@Param("keyword") String keyword);
    
    List<Notification> findByUserIdAndIsReadTrue(Long userId);
}
    boolean existsByUserIdAndMessage(Long userId, String message);
}
