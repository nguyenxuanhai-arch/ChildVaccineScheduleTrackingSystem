package edu.uth.childvaccinesystem.controllers.admin;

import edu.uth.childvaccinesystem.entities.Vaccine;
import edu.uth.childvaccinesystem.services.VaccineService;

import edu.uth.childvaccinesystem.utils.AdminUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;
import java.util.List;

@Controller
@RequestMapping("/admin/vaccines")
public class AdminVaccineController {

    private static final Logger logger = LoggerFactory.getLogger(AdminVaccineController.class);

    @Autowired
    private VaccineService vaccineService;

    @GetMapping("")
    public String vaccines(Model model) {
        List<Vaccine> vaccineList = vaccineService.getAllVaccines();
        model.addAttribute("vaccines", vaccineList);
        return "admin/vaccines/vaccines";
    }

    @GetMapping("/new")
    public String showCreateVaccineForm(Model model) {
        model.addAttribute("vaccine", new Vaccine());
        return "admin/vaccines/vaccine-form";
    }

    @GetMapping("/edit/{id}")
    public String showEditVaccineForm(@PathVariable Long id, Model model) {
        Vaccine vaccine = vaccineService.getVaccineById(id)
                .orElse(null);

        if (vaccine == null) {
            model.addAttribute("error", "Vaccine not found");
            return "admin/vaccines/vaccines"; // Return to vaccine list with error message
        }

        model.addAttribute("vaccine", vaccine);
        return "admin/vaccines/vaccine-form";
    }

    @PostMapping("/save")
    public String saveVaccine(
        @ModelAttribute Vaccine vaccine,
        @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
        RedirectAttributes redirectAttributes,
        Model model
    ) {
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                // Use AdminUtils for image compression
                byte[] compressedImage = AdminUtils.compressImage(imageFile.getBytes());
                String base64Image = Base64.getEncoder().encodeToString(compressedImage);
                vaccine.setImageBase64(base64Image);
            }

            if (vaccine.getId() == null) {
                vaccineService.createVaccine(vaccine);
                redirectAttributes.addFlashAttribute("message", "Vaccine created successfully");
            } else {
                vaccineService.updateVaccine(vaccine.getId(), vaccine);
                redirectAttributes.addFlashAttribute("message", "Vaccine updated successfully");
            }
            return "redirect:/admin/vaccines";
        } catch (Exception e) {
            logger.error("Error saving vaccine: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            model.addAttribute("vaccine", vaccine);
            return "admin/vaccines/vaccine-form";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteVaccine(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            vaccineService.deleteVaccine(id);
            redirectAttributes.addFlashAttribute("message", "Vaccine deleted successfully");
        } catch (Exception e) {
            logger.error("Error deleting vaccine: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Failed to delete vaccine: " + e.getMessage());
        }
        return "redirect:/admin/vaccines";
    }
} 