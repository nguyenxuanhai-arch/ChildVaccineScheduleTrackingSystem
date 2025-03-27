package edu.uth.childvaccinesystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import edu.uth.childvaccinesystem.models.Report;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
}
