package edu.uth.childvaccinesystem.controllers;

import edu.uth.childvaccinesystem.entities.Notification;
import edu.uth.childvaccinesystem.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/user/notifications/api")
public class NotificationApiController {

    @Autowired
    private NotificationService notificationService;

    /**
     * Endpoint để lấy thông báo của người dùng theo ID
     * Được gọi từ dropdown thông báo trong header
     */
    @GetMapping("/{userId}")
    public ResponseEntity<?> getNotificationsByUserId(@PathVariable Long userId) {
        try {
            // Kiểm tra xác thực và quyền truy cập
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
                return ResponseEntity.status(401).body(Collections.emptyList());
            }
            
            // Lấy thông báo từ service
            List<Notification> notifications = notificationService.getNotificationsByUser(userId);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Collections.emptyList());
        }
    }
    
    /**
     * Endpoint để đánh dấu một thông báo là đã đọc
     */
    @PostMapping("/mark-read/{notificationId}")
    public ResponseEntity<?> markNotificationAsRead(@PathVariable Long notificationId) {
        try {
            // Kiểm tra xác thực và quyền truy cập
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
                return ResponseEntity.status(401).body(false);
            }
            
            // Đánh dấu thông báo đã đọc
            notificationService.markNotificationAsRead(notificationId);
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(false);
        }
    }
    
    /**
     * Endpoint để đánh dấu tất cả thông báo của người dùng là đã đọc
     */
    @PostMapping("/mark-all-read/{userId}")
    public ResponseEntity<?> markAllNotificationsAsRead(@PathVariable Long userId) {
        try {
            // Kiểm tra xác thực và quyền truy cập
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
                return ResponseEntity.status(401).body(false);
            }
            
            // Đánh dấu tất cả thông báo đã đọc
            notificationService.markAllNotificationsAsRead(userId);
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(false);
        }
    }
}
