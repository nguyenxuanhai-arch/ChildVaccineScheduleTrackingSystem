package edu.uth.childvaccinesystem.controllers.admin;

import edu.uth.childvaccinesystem.services.impl.AppointmentService;
import edu.uth.childvaccinesystem.services.impl.PaymentService;
import edu.uth.childvaccinesystem.services.impl.VaccineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/admin/reports")
public class AdminReportController {

    private static final Logger logger = LoggerFactory.getLogger(AdminReportController.class);

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private VaccineService vaccineService;

    @GetMapping("")
    public String reports(Model model) {
        // Add any data needed for the reports page
        return "admin/reports/reports";
    }

    // Add more report-specific endpoints as needed
} 