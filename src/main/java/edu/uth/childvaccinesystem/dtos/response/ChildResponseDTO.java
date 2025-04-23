package edu.uth.childvaccinesystem.dtos.response;

import java.time.LocalDate;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChildResponseDTO {
    private Long id;
    private String name;
    private LocalDate dob;
    private String gender;
    private Integer ageInMonths;
    
    // Default constructor
    public ChildResponseDTO() {
    }
    
    // Constructor with parameters
    public ChildResponseDTO(Long id, String name, LocalDate dob, String gender, Integer ageInMonths) {
        this.id = id;
        this.name = name;
        this.dob = dob;
        this.gender = gender;
        this.ageInMonths = ageInMonths;
    }
} 