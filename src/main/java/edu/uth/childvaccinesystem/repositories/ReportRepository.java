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

    // Tìm báo cáo theo ID người dùng
    List<Report> findByUserId(Long userId);

    // Tìm báo cáo được tạo sau một thời điểm cụ thể
    @Query("SELECT r FROM Report r WHERE r.createdDate > :date")
    List<Report> findByCreatedDateAfter(@Param("date") LocalDateTime date);

    // Tìm báo cáo theo tiêu đề chứa từ khóa
    @Query("SELECT r FROM Report r WHERE LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Report> findByTitleContaining(@Param("keyword") String keyword);

    // Tìm báo cáo theo trạng thái
    List<Report> findByStatus(String status);

    // Tìm báo cáo theo ID người dùng và trạng thái
    List<Report> findByUserIdAndStatus(Long userId, String status);
}