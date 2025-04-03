package edu.uth.childvaccinesystem.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/layout")
    public String layoutPage() {
        return "master/_layout"; // Trả về layout
    }

    @GetMapping("/about")
    public String aboutPage() {
        return "index"; // Trả về trang About
    }

    @GetMapping("/home")
    public String homePage() {
        return "home"; // Trả về trang Home
    }
}
