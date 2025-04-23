package edu.uth.childvaccinesystem.controllers.admin;

import edu.uth.childvaccinesystem.entities.User;
import edu.uth.childvaccinesystem.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private static final Logger logger = LoggerFactory.getLogger(AdminUserController.class);

    @Autowired
    private UserService userService;

    @GetMapping("")
    public String users(Model model) {
        List<User> userList = userService.getAllUsers();
        model.addAttribute("users", userList);
        return "admin/users/users";
    }

    @GetMapping("/{id}")
    public String getUserById(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id).orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);
        return "admin/users/user-details";
    }

    @GetMapping("/new")
    public String showCreateUserForm(Model model, @RequestParam(required = false) String role) {
        User user = new User();
        
        // If role is specified in query string, set as default
        if (role != null && !role.isEmpty()) {
            if (!role.startsWith("ROLE_")) {
                role = "ROLE_" + role;
            }
            user.setRole(role);
            
            // Add hint message
            model.addAttribute("roleHint", "Creating user with role: " + role);
        }
        
        model.addAttribute("user", user);
        return "admin/users/user-form";
    }

    @GetMapping("/edit/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id).orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);
        return "admin/users/user-form";
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute User user, Model model) {
        try {
            // Set createdAt for new users to avoid NOT NULL error
            if (user.getId() == null) {
                user.setCreatedAt(LocalDateTime.now());
                logger.info("Setting createdAt for new user: {}", user.getUsername());
            }
            
            userService.createUser(user);
            return "redirect:/admin/users";
        } catch (RuntimeException e) {
            model.addAttribute("user", user);
            model.addAttribute("error", e.getMessage());
            return "admin/users/user-form";
        }
    }

    @PostMapping("")
    public String createUser(@ModelAttribute User user) {
        userService.createUser(user);
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute User userDetails) {
        userService.updateUser(id, userDetails);
        return "redirect:/admin/users";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }
} 