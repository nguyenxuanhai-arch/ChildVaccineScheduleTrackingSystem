package edu.uth.childvaccinesystem.services;

import edu.uth.childvaccinesystem.entities.Notification;
import edu.uth.childvaccinesystem.repositories.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    // Gửi thông báo
    public String sendNotification(Long userId, String message) {
        // Kiểm tra xem thông báo đã tồn tại chưa
        if (notificationRepository.existsByUserIdAndMessage(userId, message)) {
            return "Notification already exists for this user and message";
        }
        
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setMessage(message);
        notification.setStatus(true); // Đánh dấu đã gửi

        notificationRepository.save(notification);
        return "Notification sent successfully";
    }

    // Lấy danh sách thông báo của một người dùng
    public List<Notification> getNotificationsByUser(Long userId) {
        return notificationRepository.findByUserId(userId);
    }
}
