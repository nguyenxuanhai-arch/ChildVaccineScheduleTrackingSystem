package edu.uth.childvaccinesystem.controllers;

import edu.uth.childvaccinesystem.entities.Report;
import edu.uth.childvaccinesystem.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    // Create Report
    @PostMapping
    public String createReport(@RequestBody Report report) {
        return reportService.createReport(report);
    }

    // Read All Reports
    @GetMapping
    public List<Report> getAllReports() {
        return reportService.getAllReports();
    }

    // Read One Report
    @GetMapping("/{id}")
    public Optional<Report> getReportById(@PathVariable Long id) {
        return reportService.getReportById(id);
    }

    // Update Report
    @PutMapping("/{id}")
    public String updateReport(@PathVariable Long id, @RequestBody Report reportDetails) {
        return reportService.updateReport(id, reportDetails);
    }

    // Delete Report
    @DeleteMapping("/{id}")
    public void deleteReport(@PathVariable Long id) {
        reportService.deleteReport(id);
    }
}
