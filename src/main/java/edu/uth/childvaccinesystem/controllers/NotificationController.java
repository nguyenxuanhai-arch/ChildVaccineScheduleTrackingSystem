package edu.uth.childvaccinesystem.controllers;

import edu.uth.childvaccinesystem.dtos.request.NotificationRequest;
import edu.uth.childvaccinesystem.entities.Notification;
import edu.uth.childvaccinesystem.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // API gửi thông báo
    @PostMapping("/send")
    public String sendNotification(@RequestBody NotificationRequest request) {
    return notificationService.sendNotification(request.getUserId(), request.getMessage());
    }

    // API lấy danh sách thông báo của một người dùng
    @GetMapping("/{userId}")
    public List<Notification> getNotifications(@PathVariable Long userId) {
        return notificationService.getNotificationsByUser(userId);
    }
}
