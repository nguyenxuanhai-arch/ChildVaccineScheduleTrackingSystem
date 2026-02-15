package edu.uth.childvaccinesystem.controllers.admin;

import edu.uth.childvaccinesystem.entities.Vaccine;
import edu.uth.childvaccinesystem.entities.VaccinePackage;
import edu.uth.childvaccinesystem.services.impl.VaccineService;
import edu.uth.childvaccinesystem.services.impl.VaccinePackageService;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin/packages")
public class AdminPackageController {

    private static final Logger logger = LoggerFactory.getLogger(AdminPackageController.class);

    @Autowired
    private VaccinePackageService vaccinePackageService;

    @Autowired
    private VaccineService vaccineService;

    @GetMapping("")
    public String packages(Model model) {
        List<VaccinePackage> packageList = vaccinePackageService.getAllPackages();
        model.addAttribute("packages", packageList);
        return "admin/packages/packages";
    }

    @GetMapping("/individual")
    public String individualPackages(Model model) {
        List<VaccinePackage> packageList = vaccinePackageService.getPackagesByType(VaccinePackage.PackageType.INDIVIDUAL);
        model.addAttribute("packages", packageList);
        model.addAttribute("packageType", "Individual");
        return "admin/packages/packages";
    }

    @GetMapping("/standard")
    public String standardPackages(Model model) {
        List<VaccinePackage> packageList = vaccinePackageService.getPackagesByType(VaccinePackage.PackageType.PACKAGE);
        model.addAttribute("packages", packageList);
        model.addAttribute("packageType", "Standard");
        return "admin/packages/packages";
    }

    @GetMapping("/custom")
    public String customPackages(Model model) {
        List<VaccinePackage> packageList = vaccinePackageService.getPackagesByType(VaccinePackage.PackageType.CUSTOM);
        model.addAttribute("packages", packageList);
        model.addAttribute("packageType", "Custom");
        return "admin/packages/packages";
    }

    @GetMapping("/new")
    public String showCreatePackageForm(Model model) {
        model.addAttribute("package", new VaccinePackage());
        model.addAttribute("vaccines", vaccineService.getAllVaccines());
        model.addAttribute("packageTypes", VaccinePackage.PackageType.values());
        return "admin/packages/package-form";
    }

    @GetMapping("/edit/{id}")
    public String showEditPackageForm(@PathVariable Long id, Model model) {
        VaccinePackage vaccinePackage = vaccinePackageService.getPackageById(id)
                .orElse(null);

        if (vaccinePackage == null) {
            model.addAttribute("error", "Package not found");
            return "admin/packages/packages"; 
        }

        model.addAttribute("package", vaccinePackage);
        model.addAttribute("vaccines", vaccineService.getAllVaccines());
        model.addAttribute("packageTypes", VaccinePackage.PackageType.values());
        return "admin/packages/package-form";
    }

    @PostMapping("/save")
    public String savePackage(
        @ModelAttribute VaccinePackage vaccinePackage,
        @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
        @RequestParam(value = "selectedVaccineIds", required = false) List<Long> vaccineIds,
        RedirectAttributes redirectAttributes,
        Model model
    ) {
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                // Use AdminUtils for image compression
                byte[] compressedImage = AdminUtils.compressImage(imageFile.getBytes());
                String base64Image = Base64.getEncoder().encodeToString(compressedImage);
                vaccinePackage.setImageBase64(base64Image);
            }

            // Handle vaccines
            if (vaccineIds != null && !vaccineIds.isEmpty()) {
                Set<Vaccine> vaccines = new HashSet<>();
                for (Long vaccineId : vaccineIds) {
                    vaccineService.getVaccineById(vaccineId).ifPresent(vaccines::add);
                }
                vaccinePackage.setVaccines(vaccines);
            }

            if (vaccinePackage.getId() == null) {
                vaccinePackageService.createPackage(vaccinePackage);
                redirectAttributes.addFlashAttribute("message", "Package created successfully");
            } else {
                vaccinePackageService.updatePackage(vaccinePackage.getId(), vaccinePackage);
                redirectAttributes.addFlashAttribute("message", "Package updated successfully");
            }
            return "redirect:/admin/packages";
        } catch (Exception e) {
            logger.error("Error saving package: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            model.addAttribute("package", vaccinePackage);
            model.addAttribute("vaccines", vaccineService.getAllVaccines());
            model.addAttribute("packageTypes", VaccinePackage.PackageType.values());
            return "admin/packages/package-form";
        }
    }

    @PostMapping("/delete/{id}")
    public String deletePackage(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            vaccinePackageService.deletePackage(id);
            redirectAttributes.addFlashAttribute("message", "Package deleted successfully");
        } catch (Exception e) {
            logger.error("Error deleting package: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Failed to delete package: " + e.getMessage());
        }
        return "redirect:/admin/packages";
    }
} 