package mstm.muasamthongminh.muasamthongminh.modules.products.mapper;

import mstm.muasamthongminh.muasamthongminh.modules.auth.model.User;
import mstm.muasamthongminh.muasamthongminh.modules.brands.model.Brands;
import mstm.muasamthongminh.muasamthongminh.modules.categories.model.Categories;
import mstm.muasamthongminh.muasamthongminh.modules.products.dto.ProductDto;
import mstm.muasamthongminh.muasamthongminh.modules.products.enums.ProductStatus;
import mstm.muasamthongminh.muasamthongminh.modules.products.model.Products;

public class ProductMapper {
    public static Products toEntity(ProductDto dto, User user, Brands brands, Categories categories) {
        if (dto == null) return null;
        if (user == null) return null;

        return Products.builder()
                .id(dto.getId())
                .name(dto.getName())
                .skuBase(dto.getSkuBase())
                .shortDescription(dto.getShortDescription())
                .longDescription(dto.getLongDescription())
                .brandId(brands)
                .originalPrice(dto.getOriginalPrice())
                .sellingPrice(dto.getSellingPrice())
                .categoryId(categories)
                .mainImageUrl(dto.getMainImageUrl())
                .status(ProductStatus.ACTIVE)
                .slug(dto.getSlug())
                .createdByUserId(user)
                .updatedByUserId(user)
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }

    public static ProductDto toDto(Products entity) {
        if (entity == null) return null;

        ProductDto dto = new ProductDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setSkuBase(entity.getSkuBase());
        dto.setShortDescription(entity.getShortDescription());
        dto.setLongDescription(entity.getLongDescription());
        dto.setBrandId(entity.getBrandId() != null ? entity.getBrandId().getId() : null);
        dto.setOriginalPrice(entity.getOriginalPrice());
        dto.setSellingPrice(entity.getSellingPrice());
        dto.setCategoryId(entity.getCategoryId() != null ? entity.getCategoryId().getId() : null);
        dto.setMainImageUrl(entity.getMainImageUrl());
        dto.setSlug(entity.getSlug());
        dto.setCreatedByUserId(entity.getCreatedByUserId() != null ? entity.getCreatedByUserId().getId() : null);
        dto.setUpdatedByUserId(entity.getUpdatedByUserId() != null ? entity.getUpdatedByUserId().getId() : null);
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
