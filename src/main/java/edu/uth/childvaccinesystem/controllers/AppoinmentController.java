package edu.uth.childvaccinesystem.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import edu.uth.childvaccinesystem.entities.Appointment;
import edu.uth.childvaccinesystem.dtos.request.AppointmentRequest;
import edu.uth.childvaccinesystem.dtos.request.PackageAppointmentRequest;
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
    @ResponseBody
    public ResponseEntity<Appointment> bookAppointment(@RequestBody AppointmentRequest request) {
        try {
            Appointment bookedAppointment = appointmentService.bookAppointment(request);
            return ResponseEntity.ok(bookedAppointment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().header("error", e.getMessage()).build();
        }
    }
    
    @PostMapping("/book-package")
    @ResponseBody
    public ResponseEntity<Appointment> bookPackageAppointment(@RequestBody PackageAppointmentRequest request) {
        try {
            Appointment bookedAppointment = appointmentService.bookPackageAppointment(request);
            return ResponseEntity.ok(bookedAppointment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().header("error", e.getMessage()).build();
        }
    }

    @PutMapping("/{id}/cancel")
    @ResponseBody
    public ResponseEntity<Void> cancelAppointment(@PathVariable Long id) {
        try {
            appointmentService.cancelAppointment(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().header("error", e.getMessage()).build();
        }
    }
    
    @GetMapping("/list")
    @ResponseBody
    public ResponseEntity<Iterable<Appointment>> getAllAppointments() {
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }
    
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Appointment> getAppointmentById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(appointmentService.getAppointmentById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
