package edu.uth.childvaccinesystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import edu.uth.childvaccinesystem.entities.Vaccine;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VaccineRepository extends JpaRepository<Vaccine, Long> {

    List<Vaccine> findByNameIgnoreCase(String name);

    List<Vaccine> findByManufacturerIgnoreCase(String manufacturer);

    List<Vaccine> findByLotNumber(String lotNumber);

    @Query("SELECT v FROM Vaccine v WHERE v.expirationDate < :date")
    List<Vaccine> findExpiredVaccines(@Param("date") LocalDate date);

    @Query("SELECT v FROM Vaccine v WHERE v.createdAt BETWEEN :startDate AND :endDate")
    List<Vaccine> findByCreatedAtBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT v FROM Vaccine v WHERE LOWER(v.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(v.manufacturer) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Vaccine> searchByNameOrManufacturer(@Param("keyword") String keyword);

    long countByUsedTrue();

    @Query("SELECT v FROM Vaccine v WHERE v.used = false")
    List<Vaccine> findUnusedVaccines();

    List<Vaccine> findByUsed(boolean used);
    boolean existsByName(String name);
}