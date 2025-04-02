package edu.uth.childvaccinesystem.services;

import edu.uth.childvaccinesystem.entities.Report;
import edu.uth.childvaccinesystem.repositories.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    // Create
    @Transactional
    public String createReport(Report report) {
        // Check if a report with the same childId and vaccineId already exists
        if (reportRepository.existsByChildIdAndVaccineId(report.getChildId(), report.getVaccineId())) {
            return "Report already exists for this child and vaccine";
        }
        report.setCreatedAt(LocalDateTime.now());
        report.setUpdatedAt(LocalDateTime.now());
        reportRepository.save(report);
        return "Report created successfully";
    }

    // Read All
    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    // Read One
    public Optional<Report> getReportById(Long id) {
        return reportRepository.findById(id);
    }

    // Update
    @Transactional
    public String updateReport(Long id, Report reportDetails) {
        Optional<Report> report = reportRepository.findById(id);
        if (report.isPresent()) {
            Report existingReport = report.get();
            existingReport.setTitle(reportDetails.getTitle());
            existingReport.setContent(reportDetails.getContent());
            existingReport.setUpdatedAt(LocalDateTime.now());
            reportRepository.save(existingReport);
            return "Report updated successfully";
        }
        return "Report not found";
    }

    // Delete
    @Transactional
    public String deleteReport(Long id) {
        reportRepository.deleteById(id);
        return "Report deleted successfully";
    }
}