package edu.uth.childvaccinesystem.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
public class Appoinment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String status;
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "child_id", nullable = false)
    private Child child;
}
