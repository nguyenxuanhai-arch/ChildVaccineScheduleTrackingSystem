package edu.uth.childvaccinesystem.controllers.admin;

import edu.uth.childvaccinesystem.entities.Appointment;
import edu.uth.childvaccinesystem.entities.Payment;
import edu.uth.childvaccinesystem.services.impl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private VaccineService vaccineService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ChildService childService;

    @GetMapping("/login")
    public String login() {
        return "admin/login";
    }

    @GetMapping()
    public String dashboard(Model model) {
        // User statistics
        model.addAttribute("totalUsers", userService.getAllUsers().size());
        model.addAttribute("userCount", userService.getAllUsers().size());

        // Vaccine statistics
        model.addAttribute("totalVaccines", vaccineService.getAllVaccines().size());
        
        // Appointment statistics
        List<Appointment> allAppointments = appointmentService.getAllAppointments();
        model.addAttribute("totalAppointments", allAppointments.size());
        model.addAttribute("todayAppointments", allAppointments.stream()
            .filter(a -> a.getAppointmentDate() != null && a.getAppointmentDate().equals(LocalDate.now()))
            .count());
        model.addAttribute("pendingAppointments", allAppointments.stream()
            .filter(a -> a.getStatus() == Appointment.AppointmentStatus.SCHEDULED)
            .count());

        // Payment statistics
        List<Payment> allPayments = paymentService.getAllPayments();
        model.addAttribute("totalPayments", allPayments.size());
        model.addAttribute("totalRevenue", allPayments.stream()
            .filter(p -> "COMPLETED".equals(p.getStatus()))
            .mapToDouble(Payment::getAmount)
            .sum());
        model.addAttribute("pendingPayments", allPayments.stream()
            .filter(p -> "PENDING".equals(p.getStatus()))
            .count());

        // Child statistics
        model.addAttribute("totalChildren", childService.getAllChildren().size());
        
        return "admin/dashboard";
    }
} 