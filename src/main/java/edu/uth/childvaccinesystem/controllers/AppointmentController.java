package edu.uth.childvaccinesystem.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import edu.uth.childvaccinesystem.entities.Appointment;
import edu.uth.childvaccinesystem.dtos.request.AppointmentRequest;
import edu.uth.childvaccinesystem.dtos.request.PackageAppointmentRequest;
import edu.uth.childvaccinesystem.services.AppointmentService;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @GetMapping
    public String appointmentsPage() {
        return "appointments/appointments"; // đúng đường dẫn tới file HTML bạn để trong templates
    }

    @PostMapping("/book")
    @ResponseBody
    public ResponseEntity<?> bookAppointment(@RequestBody AppointmentRequest request) {
        try {
            Appointment bookedAppointment = appointmentService.bookAppointment(request);
            // Return the appointment id and redirect URL for client-side redirect
            return ResponseEntity.ok(
                java.util.Map.of(
                    "id", bookedAppointment.getId(),
                    "success", true,
                    "redirectUrl", "/payments/confirm/" + bookedAppointment.getId()
                )
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                java.util.Map.of(
                    "success", false,
                    "message", e.getMessage()
                )
            );
        }
    }
    
    @PostMapping("/book-package")
    @ResponseBody
    public ResponseEntity<?> bookPackageAppointment(@RequestBody PackageAppointmentRequest request) {
        try {
            Appointment bookedAppointment = appointmentService.bookPackageAppointment(request);
            // Return the appointment id and redirect URL for client-side redirect
            return ResponseEntity.ok(
                java.util.Map.of(
                    "id", bookedAppointment.getId(),
                    "success", true,
                    "redirectUrl", "/payments/confirm/" + bookedAppointment.getId()
                )
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                java.util.Map.of(
                    "success", false,
                    "message", e.getMessage()
                )
            );
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
