package edu.uth.childvaccinesystem.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class HomeController {

    @GetMapping("/layout")
    public String layoutPage() {
        return "master/_layout"; // Trả về layout
    }

    @GetMapping("/")
    public String homePage(Model model) {
        model.addAttribute("title", "Home - Vaccine System");
        return "index";
    }

    @GetMapping("/about")
    public String about() {
        return "about"; // Trả về trang About
    }
}
