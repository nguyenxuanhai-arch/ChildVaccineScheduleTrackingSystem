package edu.uth.childvaccinesystem.repositories;

import edu.uth.childvaccinesystem.models.Child;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ChildRepository extends JpaRepository<Child, Long> {

    // Find children by name
    List<Child> findByName(String name);

    // Find children by date of birth
    List<Child> findByDateOfBirth(LocalDate dateOfBirth);

    // Find children by name and date of birth
    List<Child> findByNameAndDateOfBirth(String name, LocalDate dateOfBirth);

    // Find children by parent's ID
    List<Child> findByParentId(Long parentId);
}