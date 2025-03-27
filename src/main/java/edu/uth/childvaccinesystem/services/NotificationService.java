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
    public Notification sendNotification(Long userId, String message) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setMessage(message);
        notification.setStatus(true); // Đánh dấu đã gửi

        return notificationRepository.save(notification);
    }

    // Lấy danh sách thông báo của một người dùng
    public List<Notification> getNotificationsByUser(Long userId) {
        return notificationRepository.findByUserId(userId);
    }
}
