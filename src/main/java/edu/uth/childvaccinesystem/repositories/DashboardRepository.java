package edu.uth.childvaccinesystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.uth.childvaccinesystem.entities.Dashboard;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DashboardRepository extends JpaRepository<Dashboard, Long> {

    List<Dashboard> findByName(String name);

    List<Dashboard> findByStatus(String status);

    List<Dashboard> findByCreatedDateAfter(LocalDate createdDate);

    List<Dashboard> findByNameAndStatus(String name, String status);

    boolean existsByName(String name);
}