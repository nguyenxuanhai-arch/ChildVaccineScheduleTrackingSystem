package edu.uth.childvaccinesystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.uth.childvaccinesystem.entities.Appointment;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    List<Appointment> findAllByDateBetween(LocalDate startDate, LocalDate endDate);

    List<Appointment> findByChildId(Long childId);

    List<Appointment> findByDate(LocalDate date);

    long countByDate(LocalDate date);

    long countByDateBetween(LocalDate startDate, LocalDate endDate);
}