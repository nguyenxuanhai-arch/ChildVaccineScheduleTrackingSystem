package edu.uth.childvaccinesystem.dtos.response;

import lombok.*;

@Data
@Builder
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
}
