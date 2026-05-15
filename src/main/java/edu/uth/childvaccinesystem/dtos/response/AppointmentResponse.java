package edu.uth.childvaccinesystem.dtos.response;

import java.time.LocalDate;

public class AppointmentResponse {
    private Long id;
    private Long childId;
    private Long vaccineId;
    private Long packageId;
    private LocalDate appointmentDate;
    private String appointmentTime;
    private String notes;
}
