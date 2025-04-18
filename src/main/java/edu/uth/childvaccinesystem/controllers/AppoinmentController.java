package edu.uth.childvaccinesystem.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import edu.uth.childvaccinesystem.entities.Appointment;
import edu.uth.childvaccinesystem.dtos.request.AppointmentRequest;
import edu.uth.childvaccinesystem.services.AppointmentService;
import org.springframework.stereotype.Controller;

@Controller
@RequestMapping("/appointments")
public class AppoinmentController {

    @Autowired
    private AppointmentService appointmentService;

    @GetMapping
    public String appointmentsPage() {
        return "appointments/appointments"; // đúng đường dẫn tới file HTML bạn để trong templates
    }

    @PostMapping("/book")
    public ResponseEntity<Appointment> bookAppointment(@RequestBody AppointmentRequest request) {
        Appointment bookedAppointment = appointmentService.bookAppointment(request);
        return ResponseEntity.ok(bookedAppointment);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelAppointment(@PathVariable Long id) {
        appointmentService.cancelAppointment(id);
        return ResponseEntity.noContent().build();
    }
}
