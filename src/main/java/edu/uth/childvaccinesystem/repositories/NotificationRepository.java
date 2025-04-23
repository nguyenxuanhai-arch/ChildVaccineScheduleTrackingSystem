package edu.uth.childvaccinesystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import edu.uth.childvaccinesystem.entities.Notification;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserId(Long userId);
    boolean existsByUserIdAndMessage(Long userId, String message);
    
    // Tìm tất cả thông báo chưa đọc của một người dùng
    List<Notification> findByUserIdAndStatusFalse(Long userId);
    
    // Đếm số lượng thông báo chưa đọc của một người dùng
    int countByUserIdAndStatusFalse(Long userId);
    
    // Thêm các phương thức mới cho admin
    
    // Đếm theo loại thông báo
    long countByType(String type);
    
    // Đếm tất cả thông báo chưa đọc
    long countByStatusFalse();
    
    // Tìm thông báo theo loại
    List<Notification> findByType(String type);
    
    // Tìm các thông báo mới nhất
    List<Notification> findTop10ByOrderBySentAtDesc();
}
