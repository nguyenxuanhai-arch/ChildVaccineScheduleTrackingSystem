package edu.uth.childvaccinesystem.services;

import edu.uth.childvaccinesystem.entities.Notification;
import edu.uth.childvaccinesystem.repositories.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.MailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Optional;

import edu.uth.childvaccinesystem.entities.User;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserService userService;

    // Gửi thông báo
    public String sendNotification(Long userId, String message) {
        // Kiểm tra xem thông báo đã tồn tại chưa
        if (notificationRepository.existsByUserIdAndMessage(userId, message)) {
            return "Notification already exists for this user and message";
        }
        
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setMessage(message);
        notification.setStatus(false); // false = chưa đọc
        
        // Xác định loại thông báo dựa trên nội dung
        if (message.contains("tiêm lẻ") || message.contains("vắc-xin")) {
            notification.setType("VACCINE");
            notification.setTitle("Thông báo tiêm vắc-xin");
        } else if (message.contains("tiêm gói") || message.contains("gói vắc-xin")) {
            notification.setType("PACKAGE");
            notification.setTitle("Thông báo gói vắc-xin");
        } else if (message.contains("thanh toán") || message.contains("VNĐ")) {
            notification.setType("PAYMENT");
            notification.setTitle("Xác nhận thanh toán");
        } else {
            notification.setType("SYSTEM");
            notification.setTitle("Thông báo hệ thống");
        }

        notificationRepository.save(notification);
        return "Notification sent successfully";
    }
    
    // Gửi thông báo với tiêu đề và loại cụ thể
    public String sendNotification(Long userId, String title, String message, String type) {
        // Kiểm tra xem thông báo đã tồn tại chưa
        if (notificationRepository.existsByUserIdAndMessage(userId, message)) {
            return "Notification already exists for this user and message";
        }
        
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setStatus(false); // false = chưa đọc

        notificationRepository.save(notification);
        return "Notification sent successfully";
    }

    // Gửi thông báo với tiêu đề và loại cụ thể, có tùy chọn gửi email
    public String sendNotification(Long userId, String title, String message, String type, Boolean sendEmail) {
        // Kiểm tra xem thông báo đã tồn tại chưa
        if (notificationRepository.existsByUserIdAndMessage(userId, message)) {
            return "Notification already exists for this user and message";
        }
        
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setStatus(false); // false = chưa đọc

        notificationRepository.save(notification);

        // Gửi email nếu được chọn
        if (sendEmail != null && sendEmail) {
            logger.debug("[DEBUG] Sending email to userId {} with title '{}', message: {}", userId, title, message);
            try {
                Optional<User> userOpt = userService.getUserById(userId);
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    String recipientEmail = user.getEmail();
                    if (recipientEmail != null && !recipientEmail.isEmpty()) {
                        SimpleMailMessage mailMessage = new SimpleMailMessage();
                        mailMessage.setTo(recipientEmail);
                        mailMessage.setSubject(title);
                        mailMessage.setText(message);
                        mailSender.send(mailMessage);
                        logger.info("Email sent to {} for notification: {}", recipientEmail, title);
                    } else {
                        logger.warn("User {} does not have a valid email address. Skipping email notification.", userId);
                    }
                } else {
                    logger.warn("User with ID {} not found. Cannot send email notification.", userId);
                }
            } catch (MailException e) {
                logger.error("Failed to send email notification to userId {}: {}", userId, e.getMessage(), e);
            } catch (Exception ex) {
                logger.error("Unexpected error during email sending for userId {}: {}", userId, ex.getMessage(), ex);
            }
        }

        return "Notification sent successfully";
    }

    // Lấy danh sách thông báo của một người dùng
    public List<Notification> getNotificationsByUser(Long userId) {
        return notificationRepository.findByUserId(userId);
    }
    
    // Đánh dấu một thông báo đã đọc
    public boolean markNotificationAsRead(Long notificationId) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            notification.setStatus(true); // true = đã đọc
            notificationRepository.save(notification);
            return true;
        }
        return false;
    }
    
    // Đánh dấu tất cả thông báo của một người dùng là đã đọc
    public int markAllNotificationsAsRead(Long userId) {
        List<Notification> unreadNotifications = notificationRepository.findByUserIdAndStatusFalse(userId);
        for (Notification notification : unreadNotifications) {
            notification.setStatus(true);
            notificationRepository.save(notification);
        }
        return unreadNotifications.size();
    }
    
    // Lấy số lượng thông báo chưa đọc của một người dùng
    public int getUnreadNotificationCount(Long userId) {
        return notificationRepository.countByUserIdAndStatusFalse(userId);
    }
}
