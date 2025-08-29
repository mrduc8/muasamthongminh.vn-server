package mstm.muasamthongminh.muasamthongminh.modules.products.repository;

import com.google.api.gax.paging.Page;
import mstm.muasamthongminh.muasamthongminh.modules.products.enums.ProductStatus;
import mstm.muasamthongminh.muasamthongminh.modules.products.model.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductsRepository extends JpaRepository<Products, Long> {

    List<Products> findByShopId_Id(Long shopId);

    @Query(value = """
        SELECT p.*
        FROM products p
        LEFT JOIN brands b     ON b.id = p.brand_id
        LEFT JOIN categories c ON c.id = p.category_id
        WHERE
          (
               REPLACE(REPLACE(p.name,'đ','d'),'Đ','D') COLLATE utf8mb4_0900_ai_ci LIKE CONCAT('%', REPLACE(REPLACE(:kw,'đ','d'),'Đ','D'), '%')
            OR REPLACE(REPLACE(COALESCE(p.meta_title,''),'đ','d'),'Đ','D') COLLATE utf8mb4_0900_ai_ci LIKE CONCAT('%', REPLACE(REPLACE(:kw,'đ','d'),'Đ','D'), '%')
            OR REPLACE(REPLACE(COALESCE(p.meta_description,''),'đ','d'),'Đ','D') COLLATE utf8mb4_0900_ai_ci LIKE CONCAT('%', REPLACE(REPLACE(:kw,'đ','d'),'Đ','D'), '%')
            OR REPLACE(REPLACE(COALESCE(b.name,''),'đ','d'),'Đ','D') COLLATE utf8mb4_0900_ai_ci LIKE CONCAT('%', REPLACE(REPLACE(:kw,'đ','d'),'Đ','D'), '%')
            OR REPLACE(REPLACE(COALESCE(c.name,''),'đ','d'),'Đ','D') COLLATE utf8mb4_0900_ai_ci LIKE CONCAT('%', REPLACE(REPLACE(:kw,'đ','d'),'Đ','D'), '%')
          )
        """,
            nativeQuery = true)
    List<Products> searchVi(@Param("kw") String keyword);

    List<Products> findByStatus(ProductStatus status);


    // Lấy theo category và trạng thái (khuyến nghị: chỉ ACTIVE cho public)
    List<Products> findByCategoryId_IdAndStatusOrderByCreatedAtDesc(Long categoryId, ProductStatus status);

    // Nếu muốn lấy mọi trạng thái trong 1 category (tuỳ chọn)
    List<Products> findByCategoryId_IdOrderByCreatedAtDesc(Long categoryId);
}
