package edu.uth.childvaccinesystem.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "children")
public class Child {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "gender")
    private String gender;
    
    @Column(name = "parent_username")
    private String parentUsername;
    
    @Column(name = "place_of_birth")
    private String placeOfBirth;
    
    @Column(name = "nationality")
    private String nationality;
    
    @Column(name = "address")
    private String address;
    
    @Column(name = "blood_type")
    private String bloodType;
    
    @Column(name = "health_insurance_number")
    private String healthInsuranceNumber;
    
    @Column(name = "allergies", columnDefinition = "TEXT")
    private String allergies;
    
    @Column(name = "medical_history", columnDefinition = "TEXT")
    private String medicalHistory;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private User parent;

    @OneToMany(mappedBy = "child", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Appointment> appointments = new HashSet<>();
}
