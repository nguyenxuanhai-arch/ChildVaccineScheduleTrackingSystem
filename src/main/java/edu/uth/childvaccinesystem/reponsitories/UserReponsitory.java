package edu.uth.childvaccinesystem.reponsitories;

import edu.uth.childvaccinesystem.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserReponsitory extends JpaRepository<User, Long> {
    User findByPhoneNumberAndEmail(String phoneNumber, String email);
}