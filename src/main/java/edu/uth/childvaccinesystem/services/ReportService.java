package edu.uth.childvaccinesystem.services;

import edu.uth.childvaccinesystem.entities.Appointment;
import edu.uth.childvaccinesystem.entities.Report;
import edu.uth.childvaccinesystem.entities.User;
import edu.uth.childvaccinesystem.entities.Vaccine;
import edu.uth.childvaccinesystem.repositories.AppointmentRepository;
import edu.uth.childvaccinesystem.repositories.ReportRepository;
import edu.uth.childvaccinesystem.repositories.UserRepository;
import edu.uth.childvaccinesystem.repositories.VaccineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private VaccineRepository vaccineRepository;

    @Autowired
    private ReportRepository reportRepository;

    public List<User> getAllUsersWithAppointments() {
        return userRepository.findAll();
    }

    public List<Vaccine> getAllUsedVaccines() {
        return vaccineRepository.findAll();
    }

    public List<Appointment> getAppointmentsForMonth(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        return appointmentRepository.findAllByDateBetween(startDate, endDate);
    }

    public Report saveReport(Report report) {
        return reportRepository.save(report);
    }
}
