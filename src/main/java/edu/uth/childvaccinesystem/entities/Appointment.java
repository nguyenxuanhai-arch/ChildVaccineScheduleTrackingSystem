package edu.uth.childvaccinesystem.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;
    
    @Enumerated(EnumType.STRING)
    private AppointmentType type;
    
    @Column(nullable = true)
    private LocalDate appointmentDate;
    
    @Column(nullable = true)
    private LocalTime appointmentTime;
    
    private LocalDate createAt;
    private LocalDate finishAt;
    private String notes;

    @ManyToOne
    @JoinColumn(name = "child_id", nullable = false)
    private Child child;

    @ManyToOne
    @JoinColumn(name = "vaccine_id")
    private Vaccine vaccine;
    
    @ManyToOne
    @JoinColumn(name = "package_id")
    private VaccinePackage vaccinePackage;

    @OneToMany(mappedBy = "appointment", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Payment> payments = new HashSet<>();
    
    public enum AppointmentStatus {
        SCHEDULED,
        COMPLETED,
        CANCELLED,
        RESCHEDULED
        // chưa làm 
    }
    
    public enum AppointmentType {
        VACCINE,
        PACKAGE
    }
}
