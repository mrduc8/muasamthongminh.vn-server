package mstm.muasamthongminh.muasamthongminh.modules.user.repository;

import mstm.muasamthongminh.muasamthongminh.modules.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("select distinct u from User u left join fetch u.roles")
    List<User> findAllWithRoles();

    @Query("select u from User u left join fetch u.roles where u.id = :id")
    Optional<User> findByIdWithRoles(@Param("id") Long id);

    // Tìm người dùng theo email hoặc tên
    List<User> findByEmailContainingIgnoreCaseOrNameContainingIgnoreCase(String email, String name);
}
