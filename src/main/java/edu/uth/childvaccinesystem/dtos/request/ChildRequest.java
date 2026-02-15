package edu.uth.childvaccinesystem.dtos.request;

import java.time.LocalDate;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChildRequest {
    private String name;
    private LocalDate dob;
    private String gender;
} 