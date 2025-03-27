package edu.uth.childvaccinesystem.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;

@Getter
@Data
@NoArgsConstructor
public class AuthResponse {
    private String token;
    public AuthResponse(String token) {
        this.token = token;
    }
    // Getter
}