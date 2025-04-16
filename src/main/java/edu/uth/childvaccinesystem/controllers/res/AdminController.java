package edu.uth.childvaccinesystem.controllers.res;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/login")
    public String login() {
        return "admin/login";
    }

    @GetMapping()
    public String dashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String users() {
        return "admin/users/users";
    }

    @GetMapping("/vaccines")
    public String vaccines() {
        return "admin/vaccines";
    }

    @GetMapping("/reports")
    public String reports() {
        return "admin/reports/reports";
    }

    @GetMapping("/payments")
    public String payments() {
        return "admin/payments/payments";
    }

    @GetMapping("/notifications")
    public String notifications() {
        return "admin/notifications/notifications";
    }

    @GetMapping("/feedbacks")
    public String feedbacks() {
        return "admin/feedbacks/feedbacks";
    }

    @GetMapping("/children")
    public String children() {
        return "admin/children/children";
    }
    
    @GetMapping("/appointments")
    public String appointments() {
        return "admin/appointments/appointments";
    }
}