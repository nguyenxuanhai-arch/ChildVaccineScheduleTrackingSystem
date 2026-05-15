package edu.uth.childvaccinesystem.mappers;

import edu.uth.childvaccinesystem.dtos.request.AppointmentRequest;
import edu.uth.childvaccinesystem.dtos.response.AppointmentResponse;
import edu.uth.childvaccinesystem.entities.Appointment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {
    Appointment toEntity(AppointmentRequest appointmentRequest);
    AppointmentResponse toResponse(Appointment appointment);
}
