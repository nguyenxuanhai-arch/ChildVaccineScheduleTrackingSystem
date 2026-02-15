package edu.uth.childvaccinesystem.dtos.request;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.lang.Nullable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackageAppointmentRequest {
    private Long childId;
    private Long packageId;
    @Nullable
    private LocalDate appointmentDate;
    @Nullable
    private LocalTime appointmentTime;
    private String notes;
    private String type;
} 