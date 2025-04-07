package edu.uth.childvaccinesystem.controllers.res;

import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;
import edu.uth.childvaccinesystem.dtos.RegisterDTO;
import edu.uth.childvaccinesystem.dtos.request.LoginRequest;
import edu.uth.childvaccinesystem.dtos.response.AuthResponse;
import edu.uth.childvaccinesystem.services.UserService;
import edu.uth.childvaccinesystem.utils.JwtUtil;
import edu.uth.childvaccinesystem.entities.User;

@RestController
@RequestMapping("/auths")
public class AuthsController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @PostMapping(value = "/login", produces = "application/json")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            UserDetails userDetails = userService.loadUserByUsername(request.getUsername());

            String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("USER");

            String token = jwtUtil.generateToken(userDetails.getUsername(), role);
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sai thông tin đăng nhập");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterDTO registerDTO) {
        String result = userService.registerUser(registerDTO);

        if (result.equals("User already exists!")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/profile")
public ResponseEntity<?> getUserProfile(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid token!");
    }

    String token = authHeader.substring(7);
    String username = jwtUtil.extractUsername(token);

    // Trả về toàn bộ thông tin User thay vì UserDetails
    User user = userService.getUserByUsername(username);

    if (user != null) {
        return ResponseEntity.ok(user);
    } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }
    }

}
