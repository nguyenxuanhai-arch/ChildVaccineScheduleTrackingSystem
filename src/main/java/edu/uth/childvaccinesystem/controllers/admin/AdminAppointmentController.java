package edu.uth.childvaccinesystem.controllers.admin;

import edu.uth.childvaccinesystem.entities.Appointment;
import edu.uth.childvaccinesystem.entities.Child;
import edu.uth.childvaccinesystem.entities.Vaccine;
import edu.uth.childvaccinesystem.entities.VaccinePackage;
import edu.uth.childvaccinesystem.services.AppointmentService;
import edu.uth.childvaccinesystem.services.ChildService;
import edu.uth.childvaccinesystem.services.VaccineService;
import edu.uth.childvaccinesystem.services.VaccinePackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Controller
@RequestMapping("/admin/appointments")
public class AdminAppointmentController {

    private static final Logger logger = LoggerFactory.getLogger(AdminAppointmentController.class);

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private ChildService childService;

    @Autowired
    private VaccineService vaccineService;

    @Autowired
    private VaccinePackageService vaccinePackageService;

    @GetMapping("")
    public String appointments(Model model) {
        try {
            List<Appointment> appointments = appointmentService.getAllAppointments();
            model.addAttribute("appointments", appointments);
        } catch (Exception e) {
            model.addAttribute("error", "Cannot load appointment list: " + e.getMessage());
            model.addAttribute("appointments", List.of()); // Return empty list
        }
        return "admin/appointments/appointments";
    }
    
    @GetMapping("/view/{id}")
    public String viewAppointmentDetails(@PathVariable Long id, Model model) {
        try {
            Appointment appointment = appointmentService.getAppointmentById(id);
            model.addAttribute("appointment", appointment);
            return "admin/appointments/appointment-details";
        } catch (Exception e) {
            model.addAttribute("error", "Cannot load appointment information: " + e.getMessage());
            return "redirect:/admin/appointments";
        }
    }
    
    @GetMapping("/{id}")
    public String getAppointmentById(@PathVariable Long id, Model model) {
        try {
            Appointment appointment = appointmentService.getAppointmentById(id);
            model.addAttribute("appointment", appointment);
            return "admin/appointments/appointment-details";
        } catch (Exception e) {
            model.addAttribute("error", "Cannot load appointment information: " + e.getMessage());
            return "redirect:/admin/appointments";
        }
    }
    
    @GetMapping("/new")
    public String showCreateAppointmentForm(@RequestParam(required = false) Long childId, Model model) {
        Appointment appointment = new Appointment();
        
        // If childId is provided, link with the child
        if (childId != null) {
            try {
                Child child = childService.getChildById(childId)
                    .orElseThrow(() -> new RuntimeException("Child profile not found"));
                appointment.setChild(child);
            } catch (Exception e) {
                model.addAttribute("error", "Child profile not found: " + e.getMessage());
            }
        }
        
        model.addAttribute("appointment", appointment);
        model.addAttribute("children", childService.getAllChildren());
        model.addAttribute("vaccines", vaccineService.getAllVaccines());
        model.addAttribute("packages", vaccinePackageService.getAllPackages());
        
        return "admin/appointments/appointment-form";
    }
    
    @GetMapping("/edit/{id}")
    public String showEditAppointmentForm(@PathVariable Long id, Model model) {
        try {
            Appointment appointment = appointmentService.getAppointmentById(id);
            model.addAttribute("appointment", appointment);
            model.addAttribute("children", childService.getAllChildren());
            model.addAttribute("vaccines", vaccineService.getAllVaccines());
            model.addAttribute("packages", vaccinePackageService.getAllPackages());
            
            return "admin/appointments/appointment-form";
        } catch (Exception e) {
            model.addAttribute("error", "Cannot load appointment information: " + e.getMessage());
            return "redirect:/admin/appointments";
        }
    }
    
    @PostMapping("/save")
    public String saveAppointment(
        @ModelAttribute Appointment appointment,
        @RequestParam(value = "childId", required = false) Long childId,
        @RequestParam(value = "vaccineId", required = false) Long vaccineId,
        @RequestParam(value = "packageId", required = false) Long packageId,
        RedirectAttributes redirectAttributes) {
        
        try {
            // Link with child
            if (childId != null) {
                Child child = childService.getChildById(childId)
                    .orElseThrow(() -> new RuntimeException("Child profile not found"));
                appointment.setChild(child);
            }
            
            // Link with vaccine or package
            if (vaccineId != null) {
                Vaccine vaccine = vaccineService.getVaccineById(vaccineId)
                    .orElseThrow(() -> new RuntimeException("Vaccine not found"));
                appointment.setVaccine(vaccine);
                // Clear package if vaccine is selected
                appointment.setVaccinePackage(null);
            } else if (packageId != null) {
                VaccinePackage vaccinePackage = vaccinePackageService.getPackageById(packageId)
                    .orElseThrow(() -> new RuntimeException("Vaccine package not found"));
                appointment.setVaccinePackage(vaccinePackage);
                // Clear vaccine if package is selected
                appointment.setVaccine(null);
            }
            
            if (appointment.getId() == null) {
                appointmentService.saveAppointment(appointment);
                redirectAttributes.addFlashAttribute("message", "Appointment created successfully");
            } else {
                appointmentService.saveAppointment(appointment);
                redirectAttributes.addFlashAttribute("message", "Appointment updated successfully");
            }
            
            return "redirect:/admin/appointments";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error saving appointment: " + e.getMessage());
            return "redirect:/admin/appointments";
        }
    }
    
    @PostMapping("/delete/{id}")
    public String deleteAppointment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            appointmentService.cancelAppointment(id);
            redirectAttributes.addFlashAttribute("message", "Appointment cancelled successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error cancelling appointment: " + e.getMessage());
        }
        return "redirect:/admin/appointments";
    }
} 