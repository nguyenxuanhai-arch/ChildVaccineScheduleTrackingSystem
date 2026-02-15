package edu.uth.childvaccinesystem.services.interfaces;

import edu.uth.childvaccinesystem.dtos.request.AppointmentRequest;
import edu.uth.childvaccinesystem.entities.Appointment;

public interface AppointmentServiceInterface {
    Appointment bookAppointment(AppointmentRequest request);
}
