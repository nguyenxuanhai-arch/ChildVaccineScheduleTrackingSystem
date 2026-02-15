package edu.uth.childvaccinesystem.dtos.request;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRequest {
    private Long childId;
    private Long vaccineId;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String notes;
}

