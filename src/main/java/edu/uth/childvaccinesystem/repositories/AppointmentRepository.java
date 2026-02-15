package edu.uth.childvaccinesystem.repositories;

import edu.uth.childvaccinesystem.entities.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByChildParentUsername(String username);
    List<Appointment> findByChildId(Long childId);
    
    @Query("SELECT DISTINCT a FROM Appointment a " +
           "LEFT JOIN FETCH a.child c " +
           "LEFT JOIN FETCH a.vaccine v " +
           "LEFT JOIN FETCH a.vaccinePackage p " +
           "LEFT JOIN FETCH p.vaccines " +
           "WHERE c.id = :childId")
    List<Appointment> findByChildIdWithDetails(@Param("childId") Long childId);
}