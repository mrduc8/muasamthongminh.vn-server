package mstm.muasamthongminh.muasamthongminh.modules.products.repository;

import mstm.muasamthongminh.muasamthongminh.modules.products.model.ProductImages;
import mstm.muasamthongminh.muasamthongminh.modules.products.model.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductImagesRepository extends JpaRepository<ProductImages, String> {
    // Sửa lại để query theo ID của Product
    List<ProductImages> findByProductId_Id(Long productId);

}
