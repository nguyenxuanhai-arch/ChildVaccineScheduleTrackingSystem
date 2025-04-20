package edu.uth.childvaccinesystem.controllers;

import edu.uth.childvaccinesystem.entities.Vaccine;
import edu.uth.childvaccinesystem.entities.VaccinePackage;
import edu.uth.childvaccinesystem.entities.VaccinePackage.PackageType;
import edu.uth.childvaccinesystem.services.VaccinePackageService;
import edu.uth.childvaccinesystem.services.VaccineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class VaccineListController {

    @Autowired
    private VaccineService vaccineService;
    
    @Autowired
    private VaccinePackageService vaccinePackageService;
    
    @GetMapping("/vaccine-list")
    public String showVaccineList(
            @RequestParam(required = false) String packageType,
            @RequestParam(required = false) Integer ageInMonths,
            Model model) {
        
        // Get featured packages
        List<VaccinePackage> featuredPackages = vaccinePackageService.getFeaturedPackages();
        model.addAttribute("featuredPackages", featuredPackages);
        
        // Get individual vaccines
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));
        List<Vaccine> individualVaccines = vaccineService.getAllVaccines();
        model.addAttribute("individualVaccines", individualVaccines);
        
        // Get package types
        List<VaccinePackage> standardPackages = vaccinePackageService.getPackagesByType(PackageType.PACKAGE);
        model.addAttribute("standardPackages", standardPackages);
        
        List<VaccinePackage> customPackages = vaccinePackageService.getPackagesByType(PackageType.CUSTOM);
        model.addAttribute("customPackages", customPackages);
        
        // Filter by age if provided
        if (ageInMonths != null) {
            List<VaccinePackage> ageSpecificPackages = vaccinePackageService.getPackagesByAge(ageInMonths);
            model.addAttribute("ageSpecificPackages", ageSpecificPackages);
        }
        
        // If a specific package type is requested, set it as active
        if (packageType != null) {
            model.addAttribute("activeTab", packageType);
        } else {
            model.addAttribute("activeTab", "featured");
        }
        
        return "vaccine/vaccine-list";
    }
} 