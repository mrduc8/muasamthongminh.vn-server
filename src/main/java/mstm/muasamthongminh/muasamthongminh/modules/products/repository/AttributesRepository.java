package mstm.muasamthongminh.muasamthongminh.modules.products.repository;

import mstm.muasamthongminh.muasamthongminh.modules.products.model.AttributeValues;
import mstm.muasamthongminh.muasamthongminh.modules.products.model.Attributes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttributesRepository extends JpaRepository<Attributes, Long> {
    // Tìm attribute theo tên
    Optional<Attributes> findByName(String name);

    // Tìm attribute theo slug
    Optional<Attributes> findBySlug(String slug);

    // Tìm tất cả attribute có tên chứa keyword
    List<Attributes> findByNameContainingIgnoreCase(String keyword);

    // Nếu bạn muốn unique check
    boolean existsByName(String name);

    boolean existsBySlug(String slug);

    List<Attributes> findByProduct_Id(Long productId);

}
