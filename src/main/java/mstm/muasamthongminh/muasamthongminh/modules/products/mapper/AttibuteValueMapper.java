package mstm.muasamthongminh.muasamthongminh.modules.products.mapper;

import mstm.muasamthongminh.muasamthongminh.modules.products.dto.AttributeValueDto;
import mstm.muasamthongminh.muasamthongminh.modules.products.model.AttributeValues;
import mstm.muasamthongminh.muasamthongminh.modules.products.model.Attributes;

public class AttibuteValueMapper {
    public static AttributeValues toEntity(AttributeValueDto dto, Attributes attributes) {
        if (dto == null) return null;

        return mstm.muasamthongminh.muasamthongminh.modules.products.model.AttributeValues.builder()
                .id(dto.getId())
                .attributeId(attributes)
                .value(dto.getValue())
                .slug(dto.getSlug())
                .colorCode(dto.getColorCode())
                .sortOrder(dto.getSortOrder())
                .imageUrl(dto.getImageUrl())
                .createdAt(dto.getCreatedAt())
                .updatedAt(attributes.getUpdatedAt())
                .build();
    }

    public static AttributeValueDto toDto(AttributeValues entity) {
        if (entity == null) return null;

        AttributeValueDto dto = new AttributeValueDto();
        dto.setId(entity.getId());
        dto.setAttributeId(entity.getAttributeId() != null ? entity.getAttributeId().getId() : null);
        dto.setValue(entity.getValue());
        dto.setSlug(entity.getSlug());
        dto.setColorCode(entity.getColorCode());
        dto.setSortOrder(entity.getSortOrder());
        dto.setImageUrl(entity.getImageUrl());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        return dto;
    }
}
