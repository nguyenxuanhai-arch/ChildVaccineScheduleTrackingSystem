package edu.uth.childvaccinesystem.controllers;

import edu.uth.childvaccinesystem.models.Appointment;
import edu.uth.childvaccinesystem.models.Report;
import edu.uth.childvaccinesystem.models.User;
import edu.uth.childvaccinesystem.models.Vaccine;
import edu.uth.childvaccinesystem.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/users-with-appointments")
    public List<User> getAllUsersWithAppointments() {
        return reportService.getAllUsersWithAppointments();
    }

    @GetMapping("/used-vaccines")
    public List<Vaccine> getAllUsedVaccines() {
        return reportService.getAllUsedVaccines();
    }

    @GetMapping("/appointments/{year}/{month}")
    public List<Appointment> getAppointmentsForMonth(@PathVariable int year, @PathVariable int month) {
        return reportService.getAppointmentsForMonth(year, month);
    }

    @PostMapping
    public ResponseEntity<Report> saveReport(@RequestBody Report report) {
        Report savedReport = reportService.saveReport(report);
        return ResponseEntity.ok(savedReport);
    }
}
