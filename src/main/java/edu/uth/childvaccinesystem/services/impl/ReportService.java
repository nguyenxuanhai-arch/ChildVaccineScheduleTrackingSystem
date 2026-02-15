package edu.uth.childvaccinesystem.services.impl;

import edu.uth.childvaccinesystem.entities.Report;
import edu.uth.childvaccinesystem.repositories.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ReportService {

    private final ReportRepository reportRepository;

    @Transactional
    public String createReport(Report report) {
        if (reportRepository.existsByChildIdAndVaccineId(report.getChildId(), report.getVaccineId())) {
            return "Report already exists for this child and vaccine";
        }
        report.setCreatedAt(LocalDateTime.now());
        report.setUpdatedAt(LocalDateTime.now());
        reportRepository.save(report);
        return "Report created successfully";
    }

    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    public Optional<Report> getReportById(Long id) {
        return reportRepository.findById(id);
    }

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

    @Transactional
    public void deleteReport(Long id) {
        reportRepository.deleteById(id);
    }
}