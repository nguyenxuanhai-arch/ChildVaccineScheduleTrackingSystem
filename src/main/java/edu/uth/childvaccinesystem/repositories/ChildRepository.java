package edu.uth.childvaccinesystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import edu.uth.childvaccinesystem.entities.Child;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ChildRepository extends JpaRepository<Child, Long> {

    List<Child> findByDob(LocalDate dob);

    List<Child> findByNameIgnoreCase(String name);

    List<Child> findByNameIgnoreCaseAndDob(String name, LocalDate dob);

    @Query("SELECT c FROM Child c WHERE c.dob BETWEEN :startDate AND :endDate")
    List<Child> findByDobBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT c FROM Child c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.parentName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Child> searchByNameOrParentName(@Param("keyword") String keyword);
}