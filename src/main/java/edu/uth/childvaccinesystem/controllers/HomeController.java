package edu.uth.childvaccinesystem.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class HomeController {

    @GetMapping("/")
    public String homePage(Model model) {
        model.addAttribute("title", "Home - Vaccine System");
        return "index";
    }

    @GetMapping("/about")
    public String about() {
        return "about"; // Trả về trang About
    }

    @GetMapping("/services")
    public String service()
    {
        return "services"; // Trả về trang Service
    }
}
