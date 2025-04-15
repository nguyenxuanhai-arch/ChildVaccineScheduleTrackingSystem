package edu.uth.childvaccinesystem.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import edu.uth.childvaccinesystem.entities.Appointment;
import edu.uth.childvaccinesystem.repositories.AppointmentRepository;
import edu.uth.childvaccinesystem.entities.Appointment.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    public Appointment bookAppointment(Appointment appointment) {
        // Thiết lập trạng thái ban đầu
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setCreateAt(LocalDateTime.now().toLocalDate());
        
        // Lưu cuộc hẹn
        return appointmentRepository.save(appointment);
    }

    public void cancelAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy cuộc hẹn"));
        
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
    }

    public List<Appointment> getAppointmentsByUsername(String username) {
        // Lấy danh sách cuộc hẹn của người dùng
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
