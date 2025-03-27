package edu.uth.childvaccinesystem.models;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Dashboard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String status;
    
    @Column(name = "created_date")
    private LocalDate createdDate;
    
    public Dashboard() {
    }

    public Dashboard(String name, String description) {
        this.name = name;
        this.description = description;
    }
}