package edu.uth.childvaccinesystem.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
public class LoginRequest {

    @NotNull(message = "Username cannot be null")
    private String username;

    @NotNull(message = "Password cannot be null")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;
    // Getter & Setter

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }
}