package edu.uth.childvaccinesystem.controllers.auth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@Controller //MVC 
public class AuthController {
    //Define register user name 
    @PostMapping("auth/register-user")
    public String postMethodName(@RequestBody String entity) {
        
        return entity;
    }
    @GetMapping("auth/register-user")
    public String RegisterUser() {
        return "auth/register";
    }
    @GetMapping("auth/login-user")
    public String LoginUser() {
        return "auth/login";
    }
    
    
}
