package edu.uth.childvaccinesystem.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Child {
    @Id
    @GeneratedValue(strategy  = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(name = "dob") 
    private LocalDate dob;
    
    @Column(name = "parent_username")
    private String parentUsername;

    @OneToMany(mappedBy = "child", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Appointment> appoinments = new HashSet<>();
}
