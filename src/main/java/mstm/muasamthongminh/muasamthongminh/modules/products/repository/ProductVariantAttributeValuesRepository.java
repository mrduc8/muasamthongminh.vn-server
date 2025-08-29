package mstm.muasamthongminh.muasamthongminh.modules.products.repository;

import mstm.muasamthongminh.muasamthongminh.modules.products.model.ProductVariantAttributeValues;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductVariantAttributeValuesRepository extends JpaRepository<ProductVariantAttributeValues, ProductVariantAttributeValues.PK> {
    List<ProductVariantAttributeValues> findByProductVariant_Id(Integer variantId);
    void deleteByProductVariantIdAndAttributeValueId(Integer variantId, Long attributeValueId);

}
