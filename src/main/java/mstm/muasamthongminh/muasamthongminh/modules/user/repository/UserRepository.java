package mstm.muasamthongminh.muasamthongminh.modules.user.repository;

import mstm.muasamthongminh.muasamthongminh.modules.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    // Tìm người dùng theo email hoặc tên
    List<User> findByEmailContainingIgnoreCaseOrNameContainingIgnoreCase(String email, String name);
}
