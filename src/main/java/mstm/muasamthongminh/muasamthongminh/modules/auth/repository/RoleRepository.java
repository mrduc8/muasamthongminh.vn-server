package mstm.muasamthongminh.muasamthongminh.modules.auth.repository;

import mstm.muasamthongminh.muasamthongminh.common.enums.Role;
import mstm.muasamthongminh.muasamthongminh.modules.auth.model.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Roles, Long> {
    Optional<Roles> findByName(Role name);

    List<Roles> findByNameIn(Collection<Role> names);
}
