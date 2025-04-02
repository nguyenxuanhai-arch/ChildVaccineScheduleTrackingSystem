package edu.uth.childvaccinesystem.services;

import edu.uth.childvaccinesystem.repositories.UserRepository;
import edu.uth.childvaccinesystem.repositories.AppointmentRepository;
import edu.uth.childvaccinesystem.repositories.VaccineRepository;
import edu.uth.childvaccinesystem.repositories.ChildRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class DashboardService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private VaccineRepository vaccineRepository;

    @Autowired
    private ChildRepository childRepository;

    public long getUserCount() {
        return userRepository.count();
    }

    public long getAppointmentCount() {
        return appointmentRepository.count();
    }

    public long getVaccineCount() {
        return vaccineRepository.count();
    }

    public long getChildCount() {
        return childRepository.count();
    }

    public long getTodayAppointmentCount() {
        LocalDate today = LocalDate.now();
        return appointmentRepository.countByDate(today);
    }

    public long getAppointmentCountBetweenDates(LocalDate startDate, LocalDate endDate) {
        return appointmentRepository.countByDateBetween(startDate, endDate);
    }

    public long getUsedVaccineCount() {
        return vaccineRepository.countByUsedTrue();
    }

    public long getNewUsersThisMonth() {
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        return userRepository.countByCreatedAtBetween(startOfMonth, endOfMonth);
    }
}