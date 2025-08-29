package mstm.muasamthongminh.muasamthongminh.modules.products.mapper;

import mstm.muasamthongminh.muasamthongminh.modules.products.dto.ProductImageDto;
import mstm.muasamthongminh.muasamthongminh.modules.products.model.ProductImages;
import mstm.muasamthongminh.muasamthongminh.modules.products.model.Products;

public class ProductImageMapper {
    public static ProductImages toEntity(ProductImageDto dto, Products products) {
        if (dto == null) return null;
        if (products == null) return null;

        return mstm.muasamthongminh.muasamthongminh.modules.products.model.ProductImages.builder()
                .id(dto.getId())
                .productId(products)
                .imageUrl(dto.getImageUrl())
                .sortOrder(dto.getSortOrder())
                .altText(dto.getAltText())
                .createdAt(dto.getCreatedAt())
                .build();
    }

    public static ProductImageDto toDto(ProductImages entity) {
        if (entity == null) return null;

        ProductImageDto dto = new ProductImageDto();
        dto.setId(entity.getId());
        dto.setProductId(entity.getProductId() != null ? entity.getProductId().getId() : null);
        dto.setImageUrl(entity.getImageUrl());
        dto.setSortOrder(entity.getSortOrder());
        dto.setAltText(entity.getAltText());
        dto.setCreatedAt(entity.getCreatedAt());

        return dto;
    }
}
