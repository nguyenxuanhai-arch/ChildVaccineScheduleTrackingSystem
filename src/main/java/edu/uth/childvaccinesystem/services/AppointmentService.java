package edu.uth.childvaccinesystem.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import edu.uth.childvaccinesystem.entities.Appointment;
import edu.uth.childvaccinesystem.entities.Appointment.AppointmentStatus;
import edu.uth.childvaccinesystem.entities.Child;
import edu.uth.childvaccinesystem.entities.Vaccine;
import edu.uth.childvaccinesystem.dtos.request.AppointmentRequest;
import edu.uth.childvaccinesystem.repositories.AppointmentRepository;
import edu.uth.childvaccinesystem.repositories.ChildRepository;
import edu.uth.childvaccinesystem.repositories.VaccineRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private VaccineRepository vaccineRepository;

    public Appointment bookAppointment(AppointmentRequest request) {
        Child child = childRepository.findById(request.getChildId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy trẻ em"));

        Vaccine vaccine = vaccineRepository.findById(request.getVaccineId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vắc xin"));

        Appointment appointment = new Appointment();
        appointment.setChild(child);
        appointment.setVaccine(vaccine);
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setAppointmentTime(request.getAppointmentTime());
        appointment.setNotes(request.getNotes());
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setCreateAt(LocalDate.now());

        return appointmentRepository.save(appointment);
    }

    public void cancelAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cuộc hẹn"));

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
    }

    public List<Appointment> getAppointmentsByUsername(String username) {
        return appointmentRepository.findByChildParentUsername(username);
    }

    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cuộc hẹn"));
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }
}
