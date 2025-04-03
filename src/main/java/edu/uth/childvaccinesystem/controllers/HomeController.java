package edu.uth.childvaccinesystem.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class HomeController {

    @GetMapping("/")
    public String layoutPage() {
        return "master/_layout"; // Trả về layout
    }

    @GetMapping("/index")
    public String aboutPage() {
        return "index"; // Trả về trang About
    }

    @GetMapping("/home")
    public String homePage(Model model) {
        model.addAttribute("title", "Home - Vaccine System");
        return "home";
    }

    @GetMapping("/about")
    public String about() {
        return "about"; // Trả về trang About
    }
}
