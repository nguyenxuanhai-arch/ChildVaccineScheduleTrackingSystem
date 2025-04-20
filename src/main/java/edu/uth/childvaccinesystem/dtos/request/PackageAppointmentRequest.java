package edu.uth.childvaccinesystem.dtos.request;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class PackageAppointmentRequest {
    private Long childId;
    private Long packageId;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String notes;
    private String type;
} 