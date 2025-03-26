package edu.uth.childvaccinesystem.repositories;

import edu.uth.childvaccinesystem.models.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Find all appointments between two dates
    List<Appointment> findAllByDateBetween(LocalDate startDate, LocalDate endDate);

    // Find all appointments for a specific child
    List<Appointment> findByChildId(Long childId);

    // Find all appointments for a specific doctor
    List<Appointment> findByDoctorId(Long doctorId);

    // Find all appointments on a specific date
    List<Appointment> findByDate(LocalDate date);
}
