package edu.uth.childvaccinesystem.controllers;

import edu.uth.childvaccinesystem.entities.Notification;
import edu.uth.childvaccinesystem.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // API gửi thông báo
    @PostMapping("/send")
    public Notification sendNotification(@RequestParam Long userId, @RequestParam String message) {
        return notificationService.sendNotification(userId, message);
    }

    // API lấy danh sách thông báo của một người dùng
    @GetMapping("/{userId}")
    public List<Notification> getNotifications(@PathVariable Long userId) {
        return notificationService.getNotificationsByUser(userId);
    }
}
