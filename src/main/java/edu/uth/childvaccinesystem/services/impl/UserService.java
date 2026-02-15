package edu.uth.childvaccinesystem.services.impl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import edu.uth.childvaccinesystem.dtos.request.RegisterRequest;
import edu.uth.childvaccinesystem.entities.User;
import edu.uth.childvaccinesystem.repositories.UserRepository;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String role = user.getRole();
        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }

        return new org.springframework.security.core.userdetails.User(
            user.getUsername(), user.getPassword(),
            Collections.singleton(new SimpleGrantedAuthority(role))
        );
    }

    public String registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            return "User already exists!";
        }

        try {
            User user = new User();
            user.setUsername(registerRequest.getUsername());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            user.setRole(registerRequest.getRole());
            user.setCreatedAt(LocalDateTime.now());

            userRepository.save(user);
            return "User registered successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Registration failed due to server error!";
        }
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    public User createUser(User user) {
        // Kiểm tra ID để phân biệt giữa tạo mới và cập nhật
        if (user.getId() == null) {
            // Đây là tạo mới - kiểm tra trùng lặp username và email
            if (userRepository.existsByUsername(user.getUsername())) {
                throw new RuntimeException("Username already exists");
            }
            
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new RuntimeException("Email already exists");
            }
            
            // Mã hóa mật khẩu cho người dùng mới
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            // Đây là cập nhật - lấy người dùng hiện tại
            User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Chỉ kiểm tra trùng lặp username nếu username thay đổi
            if (!existingUser.getUsername().equals(user.getUsername()) 
                    && userRepository.existsByUsername(user.getUsername())) {
                throw new RuntimeException("Username already exists");
            }
            
            // Chỉ kiểm tra trùng lặp email nếu email thay đổi
            if (!existingUser.getEmail().equals(user.getEmail()) 
                    && userRepository.existsByEmail(user.getEmail())) {
                throw new RuntimeException("Email already exists");
            }
            
            // Giữ nguyên mật khẩu hiện tại nếu không cung cấp mật khẩu mới
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                user.setPassword(existingUser.getPassword());
            } else {
                // Mã hóa mật khẩu mới nếu được cung cấp
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            
            // Đảm bảo giữ nguyên createdAt
            if (user.getCreatedAt() == null) {
                user.setCreatedAt(existingUser.getCreatedAt());
            }
        }
        
        return userRepository.save(user);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public void updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Kiểm tra trùng lặp username (trừ chính người dùng hiện tại)
        if (!user.getUsername().equals(userDetails.getUsername()) &&
                userRepository.existsByUsername(userDetails.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        // Kiểm tra trùng lặp email (trừ chính người dùng hiện tại)
        if (!user.getEmail().equals(userDetails.getEmail()) &&
                userRepository.existsByEmail(userDetails.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        user.setRole(userDetails.getRole());
        user.setAddress(userDetails.getAddress());
        user.setPhone(userDetails.getPhone());
        user.setName(userDetails.getName());

        userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserByIdOrUsername(String idOrUsername) {
        try {
            Long id = Long.parseLong(idOrUsername);
            return userRepository.findById(id).orElse(null);
        } catch (NumberFormatException e) {
            return userRepository.findByUsername(idOrUsername).orElse(null);
        }
    }

    public List<User> getUsersByRole(String role) {
        // Kiểm tra và chuẩn hóa định dạng role
        if (role == null || role.isEmpty()) {
            return Collections.emptyList();
        }
        
        // Nếu tên vai trò không bắt đầu bằng "ROLE_", chuẩn hóa
        String normalizedRole = role;
        if (!role.startsWith("ROLE_")) {
            normalizedRole = "ROLE_" + role;
        }
        
        // Log debug
        System.out.println("Searching for users with role: " + normalizedRole + " (original: " + role + ")");
        
        // Tìm kiếm linh hoạt hơn: thử cả với và không có prefix ROLE_
        List<User> users = userRepository.findByRole(normalizedRole);
        if (users.isEmpty() && role.startsWith("ROLE_")) {
            // Nếu không tìm thấy với ROLE_ prefix, thử tìm không có prefix
            String withoutPrefix = role.substring(5); // Bỏ "ROLE_"
            System.out.println("Not found with ROLE_ prefix, trying without: " + withoutPrefix);
            users = userRepository.findByRole(withoutPrefix);
        }
        
        // Log số lượng tìm thấy
        System.out.println("Found " + users.size() + " users with role: " + role);
        
        return users;
    }
    
    /**
     * Change password for a user
     * @param username The username of the user
     * @param currentPassword The current password
     * @param newPassword The new password to set
     * @return true if password changed successfully, false otherwise
     * @throws UsernameNotFoundException if user not found
     * @throws IllegalArgumentException if current password is incorrect
     */
    public boolean changePassword(String username, String currentPassword, String newPassword) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        
        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        
        // Set new password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        return true;
    }
}
