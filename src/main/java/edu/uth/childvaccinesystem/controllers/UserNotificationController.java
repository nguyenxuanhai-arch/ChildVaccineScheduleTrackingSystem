package edu.uth.childvaccinesystem.controllers;

import edu.uth.childvaccinesystem.entities.User;
import edu.uth.childvaccinesystem.services.impl.NotificationService;
import edu.uth.childvaccinesystem.services.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;

@Controller
@RequestMapping("/user")
public class UserNotificationController {

    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private UserService userService;

    @GetMapping("/notifications")
    public String viewNotifications(Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            // Kiểm tra nếu người dùng đã đăng nhập
            if (authentication != null && authentication.isAuthenticated() && 
                !authentication.getPrincipal().equals("anonymousUser")) {
                
                // Lấy username từ principal
                String username;
                if (authentication.getPrincipal() instanceof UserDetails) {
                    username = ((UserDetails) authentication.getPrincipal()).getUsername();
                } else {
                    username = authentication.getName();
                }
                
                // Tìm thông tin user từ username
                User currentUser = userService.getUserByUsername(username);
                
                if (currentUser != null) {
                    // Lấy danh sách thông báo của người dùng hiện tại
                    model.addAttribute("notifications", notificationService.getNotificationsByUser(currentUser.getId()));
                    
                    // Đánh dấu đã xem tất cả thông báo
                    notificationService.markAllNotificationsAsRead(currentUser.getId());
                } else {
                    model.addAttribute("notifications", new ArrayList<>());
                    model.addAttribute("error", "Không tìm thấy thông tin người dùng với tên đăng nhập: " + username);
                }
            } else {
                // Trường hợp không xác định được người dùng
                model.addAttribute("notifications", new ArrayList<>());
                model.addAttribute("error", "Vui lòng đăng nhập để xem thông báo.");
            }
        } catch (Exception e) {
            // Bắt lỗi và hiển thị thông báo lỗi
            model.addAttribute("notifications", new ArrayList<>());
            model.addAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
        }
        
        return "user/notifications";
    }
} 