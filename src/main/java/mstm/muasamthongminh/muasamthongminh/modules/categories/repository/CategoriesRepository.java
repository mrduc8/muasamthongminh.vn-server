package mstm.muasamthongminh.muasamthongminh.modules.categories.repository;

import mstm.muasamthongminh.muasamthongminh.modules.categories.model.Categories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriesRepository extends JpaRepository<Categories, Long> {
    List<Categories> findByParentId(Long parentId);
}
