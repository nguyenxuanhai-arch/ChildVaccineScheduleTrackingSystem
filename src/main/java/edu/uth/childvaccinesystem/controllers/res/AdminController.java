package edu.uth.childvaccinesystem.controllers.res;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import edu.uth.childvaccinesystem.utils.JwtUtil;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Missing or invalid token!");
        }

        String token = authHeader.substring(7); // Bỏ "Bearer "
        String role = jwtUtil.extractRole(token);
        return ResponseEntity.ok("Role: " + role);
    }
}
