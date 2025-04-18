package edu.uth.childvaccinesystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import edu.uth.childvaccinesystem.entities.User;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username); // Kiểm tra tên người dùng đã tồn tại
    User findByUsername(String username);
}