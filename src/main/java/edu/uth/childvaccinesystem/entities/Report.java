package edu.uth.childvaccinesystem.entities;

import jakarta.persistence.*;
import lombok.Setter;
import lombok.Getter;
@Entity
@Setter
@Getter
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private String author;
    private String date;
}