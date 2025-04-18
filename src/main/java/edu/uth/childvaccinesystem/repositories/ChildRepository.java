package edu.uth.childvaccinesystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import edu.uth.childvaccinesystem.entities.Child;
import edu.uth.childvaccinesystem.entities.User;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ChildRepository extends JpaRepository<Child, Long> {
    boolean existsByNameAndDob(String name, LocalDate dob);
    List<Child> findByParent(User parent);  // Sửa phương thức để tìm theo đối tượng User
}
