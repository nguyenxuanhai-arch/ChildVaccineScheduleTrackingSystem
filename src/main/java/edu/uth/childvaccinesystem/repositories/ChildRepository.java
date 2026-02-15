package edu.uth.childvaccinesystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import edu.uth.childvaccinesystem.entities.Child;
import edu.uth.childvaccinesystem.entities.User;

import java.util.List;

@Repository
public interface ChildRepository extends JpaRepository<Child, Long> {
    List<Child> findByParent(User parent);
    List<Child> findByParentUsername(String parentUsername);
}
