package mstm.muasamthongminh.muasamthongminh.modules.categories.mapper;

import mstm.muasamthongminh.muasamthongminh.common.enums.CategoryStatus;
import mstm.muasamthongminh.muasamthongminh.modules.auth.model.User;
import mstm.muasamthongminh.muasamthongminh.modules.categories.dto.CategoriesDto;
import mstm.muasamthongminh.muasamthongminh.modules.categories.model.Categories;

public class CategoriesMapper {
    public static Categories toEntity(CategoriesDto dto, User user) {
        if (user == null) return null;
        if (dto == null) return null;

        return Categories.builder()
                .id(dto.getId())
                .name(dto.getName())
                .parentId(dto.getParentId())
                .sortOrder(dto.getSortOrder())
                .status(CategoryStatus.ACTIVE)
                .imageUrl(dto.getImageUrl())
                .description(dto.getDescription())
                .metaTitle(dto.getName())
                .metaDescription(dto.getDescription())
                .createdByUserId(user)
                .updatedByUserId(user)
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }

    public static CategoriesDto toDto(Categories entity) {
        if (entity == null) return null;

        CategoriesDto dto = new CategoriesDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setParentId(entity.getParentId());
        dto.setSortOrder(entity.getSortOrder());
        dto.setStatus(entity.getStatus());
        dto.setImageUrl(entity.getImageUrl());
        dto.setDescription(entity.getDescription());
        dto.setMetaTitle(entity.getMetaTitle());
        dto.setMetaDescription(entity.getMetaDescription());
        dto.setCreatedByUserId(entity.getCreatedByUserId() != null ? entity.getCreatedByUserId().getId() : null);
        dto.setUpdatedByUserId(entity.getUpdatedByUserId() != null ? entity.getUpdatedByUserId().getId() : null);
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}