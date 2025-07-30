package mstm.muasamthongminh.muasamthongminh.modules.products.mapper;

import mstm.muasamthongminh.muasamthongminh.modules.products.dto.AttributeDto;
import mstm.muasamthongminh.muasamthongminh.modules.products.model.Attributes;

public class AttributeMapper {
    public static Attributes toEntity(AttributeDto dto) {
        if (dto == null) return null;

        return Attributes.builder()
                .id(dto.getId())
                .name(dto.getName())
                .slug(dto.getSlug())
                .description(dto.getDescription())
                .status(dto.getStatus())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }

    public static AttributeDto toDto(Attributes attributes) {
        if (attributes == null) return null;
        AttributeDto dto = new AttributeDto();

        dto.setId(attributes.getId());
        dto.setName(attributes.getName());
        dto.setSlug(attributes.getSlug());
        dto.setDescription(attributes.getDescription());
        dto.setStatus(attributes.getStatus());
        dto.setCreatedAt(attributes.getCreatedAt());
        dto.setUpdatedAt(attributes.getUpdatedAt());

        return dto;
    }
}
