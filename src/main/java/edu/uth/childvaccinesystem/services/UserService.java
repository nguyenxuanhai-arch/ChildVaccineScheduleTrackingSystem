package edu.uth.childvaccinesystem.services;

import java.time.LocalDateTime;
import java.util.Collections;
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

    public void saveUser(User user) {
        userRepository.save(user);
    }
    
}
