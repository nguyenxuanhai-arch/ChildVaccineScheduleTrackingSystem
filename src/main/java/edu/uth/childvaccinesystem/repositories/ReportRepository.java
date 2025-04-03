package edu.uth.childvaccinesystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import edu.uth.childvaccinesystem.entities.Report;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    boolean existsByChildIdAndVaccineId(Long childId, Long vaccineId);
}
