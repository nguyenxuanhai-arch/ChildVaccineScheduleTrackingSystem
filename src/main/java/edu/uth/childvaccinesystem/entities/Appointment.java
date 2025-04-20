package edu.uth.childvaccinesystem.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

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
    
    private LocalDate appointmentDate;
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
