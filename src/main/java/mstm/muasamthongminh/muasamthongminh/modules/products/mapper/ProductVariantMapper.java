package mstm.muasamthongminh.muasamthongminh.modules.products.mapper;

import mstm.muasamthongminh.muasamthongminh.modules.products.dto.ProductVariantDto;
import mstm.muasamthongminh.muasamthongminh.modules.products.enums.ProductVariantStatus;
import mstm.muasamthongminh.muasamthongminh.modules.products.model.ProductVariants;
import mstm.muasamthongminh.muasamthongminh.modules.products.model.Products;

public class ProductVariantMapper {
    public static ProductVariants toEntity(ProductVariantDto dto, Products products) {
        if (dto == null) return null;

        return ProductVariants.builder()
                .id(dto.getId())
                .productId(products)
                .sku(dto.getSku())
                .originalPrice(dto.getOriginalPrice())
                .salePrice(dto.getSalePrice())
                .stockQuantity(dto.getStockQuantity())
                .status(ProductVariantStatus.ACTIVE)
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }

    public static ProductVariantDto toDto(ProductVariants entity) {
        if (entity == null) return null;

        ProductVariantDto dto = new ProductVariantDto();
        dto.setId(entity.getId());
        dto.setProductId(entity.getProductId() != null ? entity.getProductId().getId() : null);
        dto.setSku(entity.getSku());
        dto.setOriginalPrice(entity.getOriginalPrice());
        dto.setSalePrice(entity.getSalePrice());
        dto.setStockQuantity(entity.getStockQuantity());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
