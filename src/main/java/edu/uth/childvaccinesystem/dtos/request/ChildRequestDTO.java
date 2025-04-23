package edu.uth.childvaccinesystem.dtos.request;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChildRequestDTO {
    private String name;
    private LocalDate dob;
    private String gender;
    
    // Default constructor
    public ChildRequestDTO() {
    }
    
    // Constructor with parameters
    public ChildRequestDTO(String name, LocalDate dob, String gender) {
        this.name = name;
        this.dob = dob;
        this.gender = gender;
    }
} 