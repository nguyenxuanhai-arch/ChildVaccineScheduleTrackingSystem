package edu.uth.childvaccinesystem.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private String username;
    private String name;
    private String phone;
    private String email;
    private String address;
    private String role;
    private String data;
    
        // Constructor, getters, and setters

}
