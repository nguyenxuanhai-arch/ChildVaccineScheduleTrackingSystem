package edu.uth.childvaccinesystem.dtos;

import lombok.Data;

@Data
public class RegisterDTO {
    private String username;
    private String password;
    private String role;

    public RegisterDTO(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
    
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }
}