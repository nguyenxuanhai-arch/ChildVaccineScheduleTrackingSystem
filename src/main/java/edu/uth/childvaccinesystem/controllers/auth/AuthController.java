package edu.uth.childvaccinesystem.controllers.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/register")
    public String registerUser() {
        return "auth/register"; // Ensure this maps to the correct template path
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login"; // Ensure this maps to the correct template path
    }

    @GetMapping("/profile")
    public String profile() {
        return "auth/profile"; // Đường dẫn đến file profile.html
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextHolder.clearContext(); // Xóa thông tin người dùng hiện tại
        return "redirect:/"; // Điều hướng về trang chủ
    }
}
