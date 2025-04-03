package edu.uth.childvaccinesystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import edu.uth.childvaccinesystem.entities.Child;
import java.time.LocalDate;

@Repository
public interface ChildRepository extends JpaRepository<Child, Long> {
    boolean existsByNameAndDob(String name, LocalDate dob);
}