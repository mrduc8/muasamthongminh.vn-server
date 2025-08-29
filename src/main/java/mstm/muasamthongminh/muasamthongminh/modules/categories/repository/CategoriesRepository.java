package mstm.muasamthongminh.muasamthongminh.modules.categories.repository;

import mstm.muasamthongminh.muasamthongminh.common.enums.CategoryStatus;
import mstm.muasamthongminh.muasamthongminh.modules.categories.model.Categories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriesRepository extends JpaRepository<Categories, Long> {
    List<Categories> findByParentId(Long parentId);

    Optional<Categories> findById(Long id);

    // Tìm kiếm theo tên (dùng LIKE, không phân biệt hoa thường)
    @Query(
            value = "SELECT * FROM categories c " +
                    "WHERE LOWER(CONVERT(c.name USING utf8mb4)) LIKE LOWER(CONCAT('%', :keyword, '%'))",
            nativeQuery = true
    )
    List<Categories> searchByNameIgnoreVietnamese(@Param("keyword") String keyword);

    List<Categories> findByStatus(CategoryStatus status);
}
