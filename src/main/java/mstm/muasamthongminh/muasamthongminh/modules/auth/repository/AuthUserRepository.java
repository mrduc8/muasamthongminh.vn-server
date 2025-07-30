package mstm.muasamthongminh.muasamthongminh.modules.auth.repository;

import mstm.muasamthongminh.muasamthongminh.common.enums.Role;
import mstm.muasamthongminh.muasamthongminh.modules.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthUserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END " +
            "FROM User u JOIN u.roles r " +
            "WHERE u.email = :email AND r.name = :roleName")
    boolean existsByEmailAndRoleName(@Param("email") String email, @Param("roleName") Role role);

    boolean existsByEmail(String email);

    Long id(Long id);
}
