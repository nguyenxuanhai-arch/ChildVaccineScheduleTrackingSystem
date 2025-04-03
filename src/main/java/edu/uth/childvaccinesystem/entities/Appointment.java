package edu.uth.childvaccinesystem.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String status;
    private LocalDate createAt;
    private LocalDate finishAt;

    @ManyToOne
    @JoinColumn(name = "child_id", nullable = false)
    private Child child;
    
}
