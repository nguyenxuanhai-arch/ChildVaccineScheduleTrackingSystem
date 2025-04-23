package edu.uth.childvaccinesystem.controllers.admin;

import edu.uth.childvaccinesystem.services.UserService;
import edu.uth.childvaccinesystem.services.VaccineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private VaccineService vaccineService;

    @GetMapping("/login")
    public String login() {
        return "admin/login";
    }

    @GetMapping()
    public String dashboard(Model model) {
        // Add statistics to the model
        model.addAttribute("totalVaccines", vaccineService.getAllVaccines().size());
        model.addAttribute("totalUsers", userService.getAllUsers().size());
        model.addAttribute("todaySchedules", 0); // Replace with actual data when available
        model.addAttribute("pendingSchedules", 0); // Replace with actual data when available
        
        return "admin/dashboard";
    }
} 