package mstm.muasamthongminh.muasamthongminh.modules.products.mapper;

import mstm.muasamthongminh.muasamthongminh.common.enums.ProductStatus;
import mstm.muasamthongminh.muasamthongminh.modules.brands.model.Brands;
import mstm.muasamthongminh.muasamthongminh.modules.categories.model.Categories;
import mstm.muasamthongminh.muasamthongminh.modules.products.dto.ProductDto;
import mstm.muasamthongminh.muasamthongminh.modules.products.dto.ProductResponse;
import mstm.muasamthongminh.muasamthongminh.modules.products.model.AttributeValues;
import mstm.muasamthongminh.muasamthongminh.modules.products.model.ProductImages;
import mstm.muasamthongminh.muasamthongminh.modules.products.model.ProductVariants;
import mstm.muasamthongminh.muasamthongminh.modules.products.model.Products;
import mstm.muasamthongminh.muasamthongminh.modules.shop.model.Shop;

import java.util.List;
import java.util.stream.Collectors;

public class ProductMapper {
    public static Products toEntity(ProductDto dto, Shop shop, Brands brands, Categories categories) {
        if (dto == null) return null;
        if (shop == null) return null;

        return Products.builder()
                .id(dto.getId())
                .name(dto.getName())
                .shopId(shop)
                .brandId(brands)
                .categoryId(categories)
                .mainImageUrl(dto.getMainImageUrl())
                .description(dto.getDescription())
                .status(dto.getStatus())
                .slug(dto.getSlug())
                .metaTitle(dto.getMetaTitle())
                .metaDescription(dto.getMetaDescription())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }

    public static ProductDto toDto(Products entity) {
        if (entity == null) return null;
        ProductDto dto = new ProductDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setBrandId(entity.getBrandId() != null ? entity.getBrandId().getId() : null);
        dto.setShopId(entity.getShopId() != null ? entity.getShopId().getId() : null);
        dto.setCategoryId(entity.getCategoryId() != null ? entity.getCategoryId().getId() : null);
        dto.setStatus(entity.getStatus());
        dto.setDescription(entity.getDescription());
        dto.setMainImageUrl(entity.getMainImageUrl());
        dto.setMetaTitle(entity.getMetaTitle());
        dto.setMetaDescription(entity.getMetaDescription());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    public static ProductResponse toResponse(
            Products entity,
            List<ProductImages> images,
            List<ProductVariants> variants,
            List<AttributeValues> attributeValues
    ) {
        if (entity == null) return null;

        ProductResponse dto = new ProductResponse();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setCategoryId(entity.getCategoryId() != null ? entity.getCategoryId().getId() : null);
        dto.setBrandId(entity.getBrandId() != null ? entity.getBrandId().getId() : null);
        dto.setShopId(entity.getShopId() != null ? entity.getShopId().getId() : null);
        dto.setDescription(entity.getDescription());
        dto.setMainImageUrl(entity.getMainImageUrl());
        dto.setMetaTitle(entity.getMetaTitle());
        dto.setMetaDescription(entity.getMetaDescription());
        dto.setSlug(entity.getSlug());
        dto.setStatus(entity.getStatus() != null ? entity.getStatus().name() : null);
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        if (entity.getShopId() != null) {
            Shop shop = entity.getShopId();
            dto.setShopId(shop.getId());
            dto.setShopName(shop.getShopName());
            dto.setShopLogo(shop.getLogoUrl());
            dto.setShopAddress(shop.getAddress());
        }

        // Map images
        if (images != null) {
            dto.setImages(images.stream().map(img -> {
                ProductResponse.ImageResponse resp = new ProductResponse.ImageResponse();
                resp.setId(img.getId());
                resp.setImageUrl(img.getImageUrl());
                resp.setSortOrder(img.getSortOrder());
                resp.setAltText(img.getAltText());
                return resp;
            }).collect(Collectors.toList()));
        }

        // Map variants + attributes
        if (variants != null) {
            dto.setVariants(variants.stream().map(v -> {
                ProductResponse.VariantResponse resp = new ProductResponse.VariantResponse();
                resp.setId(v.getId());
                resp.setSku(v.getSku());
                resp.setOriginalPrice(v.getOriginalPrice());
                resp.setSalePrice(v.getSalePrice());
                resp.setStockQuantity(v.getStockQuantity());

                List<ProductResponse.AttributeResponse> attrList = attributeValues.stream()
                        .filter(av -> av.getVariant() != null && av.getVariant().getId().equals(v.getId()))
                        .map(av -> {
                            ProductResponse.AttributeResponse attrResp = new ProductResponse.AttributeResponse();
                            attrResp.setId(av.getId());
                            attrResp.setName(av.getAttributeId() != null ? av.getAttributeId().getName() : null);
                            attrResp.setValue(av.getValue());
                            attrResp.setSlug(av.getSlug());
                            attrResp.setColorCode(av.getColorCode());
                            attrResp.setImageUrl(av.getImageUrl());
                            return attrResp;
                        })
                        .collect(Collectors.toList());

                resp.setAttributes(attrList);
                return resp;
            }).collect(Collectors.toList()));
        }

        return dto;
    }
}
