package edu.uth.childvaccinesystem.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import edu.uth.childvaccinesystem.entities.Appointment;
import edu.uth.childvaccinesystem.repositories.AppointmentRepository;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public Appointment bookAppointment(Appointment appointment) {
        // Check if the appointment time is available
        // if (appointmentRepository.exitsCreatedAtBetween(appointment.getCreateAt(), appointment.getFinishAt())) {
        //     throw new IllegalArgumentException("Appointment time is already booked.");
        // }
        // Save the appointment to the database
        return appointmentRepository.save(appointment);
    }

    public void cancelAppointment(Long id) {
        // Delete the appointment from the database
        appointmentRepository.deleteById(id);
        System.out.println("Appointment with ID " + id + " has been canceled.");
    }
}
