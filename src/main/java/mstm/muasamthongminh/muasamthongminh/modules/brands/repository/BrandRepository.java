package mstm.muasamthongminh.muasamthongminh.modules.brands.repository;

import mstm.muasamthongminh.muasamthongminh.modules.brands.model.Brands;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brands, Long> {
    Optional<Brands> findById(Long id);
}
