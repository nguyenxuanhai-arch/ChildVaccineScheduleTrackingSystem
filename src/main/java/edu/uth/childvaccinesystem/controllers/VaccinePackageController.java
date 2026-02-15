package edu.uth.childvaccinesystem.controllers;

import edu.uth.childvaccinesystem.entities.Vaccine;
import edu.uth.childvaccinesystem.entities.VaccinePackage;
import edu.uth.childvaccinesystem.entities.VaccinePackage.PackageType;
import edu.uth.childvaccinesystem.services.impl.VaccinePackageService;
import edu.uth.childvaccinesystem.services.impl.VaccineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/packages")
public class VaccinePackageController {

    @Autowired
    private VaccinePackageService vaccinePackageService;

    @Autowired
    private VaccineService vaccineService;

    // Get all packages
    @GetMapping
    public List<VaccinePackage> getAllPackages() {
        return vaccinePackageService.getAllPackages();
    }

    // Get packages by type
    @GetMapping("/type/{type}")
    public List<VaccinePackage> getPackagesByType(@PathVariable String type) {
        return vaccinePackageService.getPackagesByType(PackageType.valueOf(type));
    }

    // Get package by id
    @GetMapping("/{id}")
    public ResponseEntity<VaccinePackage> getPackageById(@PathVariable Long id) {
        Optional<VaccinePackage> vaccinePackage = vaccinePackageService.getPackageById(id);
        return vaccinePackage.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Create a new package
    @PostMapping
    public VaccinePackage createPackage(@RequestBody VaccinePackage vaccinePackage) {
        return vaccinePackageService.createPackage(vaccinePackage);
    }

    // Update an existing package
    @PutMapping("/{id}")
    public ResponseEntity<VaccinePackage> updatePackage(
            @PathVariable Long id,
            @RequestBody VaccinePackage packageDetails) {
        
        try {
            VaccinePackage updatedPackage = vaccinePackageService.updatePackage(id, packageDetails);
            return ResponseEntity.ok(updatedPackage);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Add vaccines to a package
    @PostMapping("/{id}/vaccines")
    public ResponseEntity<VaccinePackage> addVaccinesToPackage(
            @PathVariable Long id,
            @RequestBody Map<String, List<Long>> requestBody) {
        
        List<Long> vaccineIds = requestBody.get("vaccineIds");
        if (vaccineIds == null || vaccineIds.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        Optional<VaccinePackage> packageOpt = vaccinePackageService.getPackageById(id);
        if (packageOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        VaccinePackage vaccinePackage = packageOpt.get();
        Set<Vaccine> currentVaccines = vaccinePackage.getVaccines();
        
        for (Long vaccineId : vaccineIds) {
            vaccineService.getVaccineById(vaccineId).ifPresent(currentVaccines::add);
        }
        
        vaccinePackage.setVaccines(currentVaccines);
        VaccinePackage updatedPackage = vaccinePackageService.createPackage(vaccinePackage);
        
        return ResponseEntity.ok(updatedPackage);
    }

    // Remove a vaccine from a package
    @DeleteMapping("/{packageId}/vaccines/{vaccineId}")
    public ResponseEntity<VaccinePackage> removeVaccineFromPackage(
            @PathVariable Long packageId,
            @PathVariable Long vaccineId) {
        
        Optional<VaccinePackage> packageOpt = vaccinePackageService.getPackageById(packageId);
        if (packageOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Optional<Vaccine> vaccineOpt = vaccineService.getVaccineById(vaccineId);
        if (vaccineOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        VaccinePackage vaccinePackage = packageOpt.get();
        Set<Vaccine> currentVaccines = vaccinePackage.getVaccines();
        
        if (currentVaccines.removeIf(v -> v.getId().equals(vaccineId))) {
            vaccinePackage.setVaccines(currentVaccines);
            VaccinePackage updatedPackage = vaccinePackageService.createPackage(vaccinePackage);
            return ResponseEntity.ok(updatedPackage);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    // Delete a package
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePackage(@PathVariable Long id) {
        try {
            vaccinePackageService.deletePackage(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
} 