package edu.uth.childvaccinesystem.dtos.request;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class AppointmentRequest {
    private Long childId;
    private Long vaccineId;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String notes;
}

