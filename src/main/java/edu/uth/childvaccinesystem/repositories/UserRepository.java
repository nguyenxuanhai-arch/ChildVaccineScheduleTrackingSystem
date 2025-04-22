package edu.uth.childvaccinesystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import edu.uth.childvaccinesystem.entities.User;
import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username); // Kiểm tra tên người dùng đã tồn tại
    boolean existsByEmail(String email); // Kiểm tra email đã tồn tại
    List<User> findByRole(String role); // Tìm danh sách người dùng theo vai trò
}

