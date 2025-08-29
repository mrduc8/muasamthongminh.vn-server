package mstm.muasamthongminh.muasamthongminh.modules.products.repository;

import mstm.muasamthongminh.muasamthongminh.modules.products.model.AttributeValues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttributeValuesRepository extends JpaRepository<AttributeValues, Long> {
    // Lấy tất cả value theo attributeId
    List<AttributeValues> findByAttributeId_Id(Long attributeId);

    // Tìm value cụ thể theo tên và attributeId
    Optional<AttributeValues> findByValueAndAttributeId_Id(String value, Long attributeId);

    // Kiểm tra có tồn tại value nào trong attribute đó không
    boolean existsByValueAndAttributeId_Id(String value, Long attributeId);

    List<AttributeValues> findByProduct_Id(Long productId);

    List<AttributeValues> findByVariant_Id(Long variantId);


}
