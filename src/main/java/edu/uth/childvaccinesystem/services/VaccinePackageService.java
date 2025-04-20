package edu.uth.childvaccinesystem.services;

import edu.uth.childvaccinesystem.entities.VaccinePackage;
import edu.uth.childvaccinesystem.entities.VaccinePackage.PackageType;
import edu.uth.childvaccinesystem.repositories.VaccinePackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VaccinePackageService {

    @Autowired
    private VaccinePackageRepository vaccinePackageRepository;

    public List<VaccinePackage> getAllPackages() {
        return vaccinePackageRepository.findAll();
    }

    public Page<VaccinePackage> getAllPackagesWithPagination(Pageable pageable) {
        return vaccinePackageRepository.findAll(pageable);
    }

    public Optional<VaccinePackage> getPackageById(Long id) {
        return vaccinePackageRepository.findById(id);
    }

    public List<VaccinePackage> getPackagesByType(PackageType type) {
        return vaccinePackageRepository.findByType(type);
    }

    public Page<VaccinePackage> getPackagesByType(PackageType type, Pageable pageable) {
        return vaccinePackageRepository.findByType(type, pageable);
    }

    public List<VaccinePackage> getFeaturedPackages() {
        return vaccinePackageRepository.findByFeaturedTrue();
    }

    public Page<VaccinePackage> searchPackages(String query, Pageable pageable) {
        return vaccinePackageRepository.findByNameContainingIgnoreCase(query, pageable);
    }

    public List<VaccinePackage> getPackagesByAge(int ageInMonths) {
        return vaccinePackageRepository.findByAgeRangeStartLessThanEqualAndAgeRangeEndGreaterThanEqual(
            ageInMonths, ageInMonths);
    }

    public VaccinePackage createPackage(VaccinePackage vaccinePackage) {
        return vaccinePackageRepository.save(vaccinePackage);
    }

    public VaccinePackage updatePackage(Long id, VaccinePackage packageDetails) {
        Optional<VaccinePackage> packageOpt = vaccinePackageRepository.findById(id);
        if (packageOpt.isPresent()) {
            VaccinePackage existingPackage = packageOpt.get();
            
            existingPackage.setName(packageDetails.getName());
            existingPackage.setDescription(packageDetails.getDescription());
            existingPackage.setType(packageDetails.getType());
            existingPackage.setPrice(packageDetails.getPrice());
            existingPackage.setAgeRangeStart(packageDetails.getAgeRangeStart());
            existingPackage.setAgeRangeEnd(packageDetails.getAgeRangeEnd());
            existingPackage.setFeatured(packageDetails.isFeatured());
            
            // Only update image if provided
            if (packageDetails.getImageBase64() != null && !packageDetails.getImageBase64().isEmpty()) {
                existingPackage.setImageBase64(packageDetails.getImageBase64());
            }
            
            // Update vaccines if provided
            if (packageDetails.getVaccines() != null && !packageDetails.getVaccines().isEmpty()) {
                existingPackage.setVaccines(packageDetails.getVaccines());
            }
            
            return vaccinePackageRepository.save(existingPackage);
        }
        throw new RuntimeException("Package not found with id: " + id);
    }

    public void deletePackage(Long id) {
        if (!vaccinePackageRepository.existsById(id)) {
            throw new RuntimeException("Package not found with id: " + id);
        }
        vaccinePackageRepository.deleteById(id);
    }
} 