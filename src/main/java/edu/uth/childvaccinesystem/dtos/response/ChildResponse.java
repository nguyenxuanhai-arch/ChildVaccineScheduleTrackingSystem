package edu.uth.childvaccinesystem.dtos.response;

import java.time.LocalDate;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChildResponse {
    private Long id;
    private String name;
    private LocalDate dob;
    private String gender;
    private Integer ageInMonths;
} 