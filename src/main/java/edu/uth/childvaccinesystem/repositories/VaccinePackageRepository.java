package edu.uth.childvaccinesystem.repositories;

import edu.uth.childvaccinesystem.entities.VaccinePackage;
import edu.uth.childvaccinesystem.entities.VaccinePackage.PackageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VaccinePackageRepository extends JpaRepository<VaccinePackage, Long> {
    List<VaccinePackage> findByType(PackageType type);
    List<VaccinePackage> findByFeaturedTrue();
    List<VaccinePackage> findByAgeRangeStartLessThanEqualAndAgeRangeEndGreaterThanEqual(int age, int age2);
} 