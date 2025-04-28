package edu.uth.childvaccinesystem.controllers.admin;

import edu.uth.childvaccinesystem.dtos.request.NotificationRequest;
import edu.uth.childvaccinesystem.entities.Notification;
import edu.uth.childvaccinesystem.entities.User;
import edu.uth.childvaccinesystem.repositories.NotificationRepository;
import edu.uth.childvaccinesystem.services.NotificationService;
import edu.uth.childvaccinesystem.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/admin/notifications")
public class AdminNotificationController {

    private static final Logger logger = LoggerFactory.getLogger(AdminNotificationController.class);

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserService userService;

    @GetMapping("")
    public String notifications(Model model) {
        try {
            // Basic statistics
            long totalNotifications = 0;
            try {
                totalNotifications = notificationRepository.count();
            } catch (Exception e) {
                logger.error("Error counting total notifications: {}", e.getMessage());
            }
            model.addAttribute("totalNotifications", totalNotifications);
            
            // Add statistics
            model.addAttribute("vaccineNotifications", countNotificationsByType("VACCINE"));
            model.addAttribute("packageNotifications", countNotificationsByType("PACKAGE"));
            model.addAttribute("paymentNotifications", countNotificationsByType("PAYMENT"));
            model.addAttribute("unreadNotifications", countUnreadNotifications());
            
            // Get user list
            List<User> users = new ArrayList<>();
            try {
                users = userService.getAllUsers();
            } catch (Exception e) {
                logger.error("Error loading users: {}", e.getMessage());
            }
            model.addAttribute("users", users);
            
            logger.info("Loaded notification statistics - total: {}", totalNotifications);
            
            return "admin/notifications/notifications";
        } catch (Exception e) {
            logger.error("Error loading notification page: ", e);
            model.addAttribute("error", "Error loading data: " + e.getMessage());
            // Initialize required attributes to avoid null errors
            model.addAttribute("totalNotifications", 0);
            model.addAttribute("vaccineNotifications", 0);
            model.addAttribute("packageNotifications", 0);
            model.addAttribute("paymentNotifications", 0);
            model.addAttribute("unreadNotifications", 0);
            model.addAttribute("users", new ArrayList<>());
            return "admin/notifications/notifications";
        }
    }

    // Count notifications by type
    private long countNotificationsByType(String type) {
        try {
            List<Notification> notifications = notificationRepository.findAll();
            if (notifications == null) {
                return 0;
            }
            return notifications.stream()
                    .filter(n -> type.equals(n.getType()))
                    .count();
        } catch (Exception e) {
            logger.error("Error counting notifications by type: {}", e.getMessage());
            return 0;
        }
    }
    
    // Count unread notifications
    private long countUnreadNotifications() {
        try {
            List<Notification> notifications = notificationRepository.findAll();
            if (notifications == null) {
                return 0;
            }
            return notifications.stream()
                    .filter(n -> !n.isStatus())
                    .count();
        } catch (Exception e) {
            logger.error("Error counting unread notifications: {}", e.getMessage());
            return 0;
        }
    }
    
    @GetMapping("/data")
    @ResponseBody
    public List<Notification> getAllNotifications() {
        try {
            List<Notification> notifications = notificationRepository.findAll();
            // Return empty list if null to avoid NullPointerException
            return notifications != null ? notifications : new ArrayList<>();
        } catch (Exception e) {
            logger.error("Error getting all notifications: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @PostMapping("/send")
    @ResponseBody
    public ResponseEntity<Map<String, String>> sendNotification(@RequestBody NotificationRequest request) {
        Map<String, String> response = new HashMap<>();
        try {
            if (request.getUserId() == null) {
                response.put("message", "Error: User ID cannot be empty");
                return ResponseEntity.badRequest().body(response);
            }
            
            logger.debug("Received sendEmail flag: {}", request.getSendEmail());
            String result = notificationService.sendNotification(
                    request.getUserId(),
                    request.getTitle(),
                    request.getMessage(),
                    request.getType(),
                    request.getSendEmail());
            
            response.put("message", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error sending notification: ", e);
            response.put("message", "Error sending notification: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> deleteNotification(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        
        try {
            Optional<Notification> notification = notificationRepository.findById(id);
            if (notification.isPresent()) {
                notificationRepository.delete(notification.get());
                response.put("message", "Notification deleted successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Notification not found");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            logger.error("Error deleting notification: ", e);
            response.put("message", "Error deleting notification: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PostMapping("/send-to-all")
    @ResponseBody
    public ResponseEntity<Map<String, String>> sendToAllUsers(@RequestBody NotificationRequest request) {
        Map<String, String> response = new HashMap<>();
        
        try {
            if (request.getType() == null || request.getMessage() == null) {
                response.put("message", "Notification type and content cannot be empty");
                return ResponseEntity.badRequest().body(response);
            }
            
            int count = 0;
            List<User> users = userService.getAllUsers();
            
            for (User user : users) {
                try {
                    logger.debug("Received sendEmail flag (to all): {}", request.getSendEmail());
                    notificationService.sendNotification(
                            user.getId(),
                            request.getTitle(),
                            request.getMessage(),
                            request.getType(),
                            request.getSendEmail());
                    count++;
                } catch (Exception e) {
                    logger.error("Error sending notification to user {}: {}", user.getId(), e.getMessage());
                    // Continue sending to other users
                }
            }
            
            response.put("message", "Sent notification to " + count + " users");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error sending notifications to all users: ", e);
            response.put("message", "Error sending notification: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/by-type/{type}")
    @ResponseBody
    public List<Notification> getByType(@PathVariable String type) {
        try {
            if (type == null || type.isEmpty()) {
                logger.warn("Empty notification type provided");
                return new ArrayList<>();
            }
            
            // Filter by notification type
            List<Notification> notifications = notificationRepository.findAll();
            if (notifications == null) {
                return new ArrayList<>();
            }
            
            return notifications.stream()
                    .filter(n -> type.equals(n.getType()))
                    .toList();
        } catch (Exception e) {
            logger.error("Error getting notifications by type: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @GetMapping("/users")
    @ResponseBody
    public List<Map<String, Object>> getUsers() {
        try {
            List<User> users = userService.getAllUsers();
            List<Map<String, Object>> result = new ArrayList<>();
            
            for (User user : users) {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("id", user.getId());
                userMap.put("username", user.getUsername());
                userMap.put("name", user.getName());
                userMap.put("email", user.getEmail());
                result.add(userMap);
            }
            
            return result;
        } catch (Exception e) {
            logger.error("Error getting users: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
}