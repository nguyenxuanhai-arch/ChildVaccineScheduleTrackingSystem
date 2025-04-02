package edu.uth.childvaccinesystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import edu.uth.childvaccinesystem.entities.Report;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findByUserId(Long userId);

    @Query("SELECT r FROM Report r WHERE r.createdDate > :date")
    List<Report> findByCreatedDateAfter(@Param("date") LocalDateTime date);

    @Query("SELECT r FROM Report r WHERE LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Report> findByTitleContaining(@Param("keyword") String keyword);

    List<Report> findByStatus(String status);

    List<Report> findByUserIdAndStatus(Long userId, String status);

    @Query("SELECT r FROM Report r WHERE r.createdDate BETWEEN :startDate AND :endDate")
    List<Report> findByCreatedDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}