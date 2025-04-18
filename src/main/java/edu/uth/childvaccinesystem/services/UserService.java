package edu.uth.childvaccinesystem.services;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import edu.uth.childvaccinesystem.dtos.RegisterDTO;
import edu.uth.childvaccinesystem.entities.User;
import edu.uth.childvaccinesystem.repositories.UserRepository;
import java.util.List;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String role = user.getRole();
        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role; // thêm prefix nếu thiếu
        }

        return new org.springframework.security.core.userdetails.User(
            user.getUsername(), user.getPassword(),
            Collections.singleton(new SimpleGrantedAuthority(role))
        );
    }

    public String registerUser(RegisterDTO registerDTO) {
        if (userRepository.existsByUsername(registerDTO.getUsername())) {
            return "User already exists!";
        }

        try {
            User user = new User();
            user.setUsername(registerDTO.getUsername());
            user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
            user.setRole(registerDTO.getRole());
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
        // Kiểm tra trùng lặp username
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        // Kiểm tra trùng lặp email
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Mã hóa mật khẩu nếu là người dùng mới
        if (user.getId() == null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
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
}
