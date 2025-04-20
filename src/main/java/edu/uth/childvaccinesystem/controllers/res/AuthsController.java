package edu.uth.childvaccinesystem.controllers.res;

import java.io.IOException;
import java.util.HashMap;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import edu.uth.childvaccinesystem.dtos.RegisterDTO;
import edu.uth.childvaccinesystem.dtos.request.LoginRequest;
import edu.uth.childvaccinesystem.dtos.response.AuthResponse;
import edu.uth.childvaccinesystem.dtos.response.UserProfileResponse;
import edu.uth.childvaccinesystem.services.UserService;
import org.springframework.web.multipart.MultipartFile;
import edu.uth.childvaccinesystem.utils.JwtUtil;
import edu.uth.childvaccinesystem.entities.User;
import java.util.Base64;
import java.util.Collections;

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
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
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

        // ✅ Set JWT vào cookie
        Cookie cookie = new Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60); // 1 ngày
        httpResponse.addCookie(cookie);

        // Nếu bạn dùng frontend riêng thì giữ lại dòng này:
        return ResponseEntity.ok(new AuthResponse(token));

        // Nếu login từ form thì có thể redirect:
        // return ResponseEntity.status(HttpStatus.FOUND).header("Location", "/dashboard").build();

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

    User user = userService.getUserByUsername(username);

    if (user != null) {
        String base64Image = null;
        if (user.getData() != null) {
            base64Image = Base64.getEncoder().encodeToString(user.getData()); // Chuyển byte[] thành base64
        }
        return ResponseEntity.ok(new UserProfileResponse(user.getUsername(), user.getName(), user.getPhone(), user.getEmail(), user.getAddress(), user.getRole(), base64Image));
    } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }
}

@PostMapping("/update-profile")
public ResponseEntity<?> updateProfile(
        @RequestParam("name") String name,
        @RequestParam("phone") String phone,
        @RequestParam(value = "address", required = false) String address,
        @RequestParam(value = "email", required = false) String email,
        @RequestParam(value = "image", required = false) MultipartFile image,
        HttpServletRequest request) {

    String token = request.getHeader("Authorization").substring(7);
    String username = jwtUtil.extractUsername(token);

    User user = userService.getUserByUsername(username);
    if (user == null) return ResponseEntity.status(404).body("User not found");

    user.setName(name);
    user.setPhone(phone);
    user.setEmail(email); 
    user.setAddress(address); 
    if (image != null && !image.isEmpty()) {
        try {
            user.setData(image.getBytes()); // Chuyển file ảnh thành byte[] để lưu vào cơ sở dữ liệu
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Lỗi khi đọc ảnh");
        }
    }
    userService.saveUser(user); // Ensure this method exists to persist user

    return ResponseEntity.ok("Cập nhật thành công");
}

@GetMapping("/verify-token")
    public ResponseEntity<?> verifyToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                String username = jwtUtil.extractUsername(token);
                return ResponseEntity.ok(Collections.singletonMap("username", username));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization header missing");
    }

}
