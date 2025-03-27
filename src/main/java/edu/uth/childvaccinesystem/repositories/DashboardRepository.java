package edu.uth.childvaccinesystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.uth.childvaccinesystem.entities.Dashboard;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DashboardRepository extends JpaRepository<Dashboard, Long> {

    // Tìm Dashboard theo tên
    List<Dashboard> findByName(String name);

    // Tìm Dashboard theo trạng thái
    List<Dashboard> findByStatus(String status);

    // Tìm Dashboard được tạo sau một ngày cụ thể
    List<Dashboard> findByCreatedDateAfter(LocalDate createdDate);

    // Tìm Dashboard theo tên và trạng thái
    List<Dashboard> findByNameAndStatus(String name, String status);
}