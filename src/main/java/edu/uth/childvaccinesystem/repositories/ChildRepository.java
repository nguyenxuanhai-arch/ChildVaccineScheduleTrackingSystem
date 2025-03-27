package edu.uth.childvaccinesystem.repositories;

import edu.uth.childvaccinesystem.models.Child;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ChildRepository extends JpaRepository<Child, Long> {

    List<Child> findByDob(LocalDate dob);

}