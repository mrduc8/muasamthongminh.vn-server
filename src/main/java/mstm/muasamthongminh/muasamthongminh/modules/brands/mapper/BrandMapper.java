package mstm.muasamthongminh.muasamthongminh.modules.brands.mapper;

import mstm.muasamthongminh.muasamthongminh.modules.auth.model.User;
import mstm.muasamthongminh.muasamthongminh.modules.bankaccount.dto.BankDto;
import mstm.muasamthongminh.muasamthongminh.modules.brands.dto.BrandDto;
import mstm.muasamthongminh.muasamthongminh.modules.brands.enums.BrandStatus;
import mstm.muasamthongminh.muasamthongminh.modules.brands.model.Brands;

public class BrandMapper {
    public static Brands toEntity(BrandDto dto, User user) {
        if (dto == null) return null;
        if (user == null) return null;

        return Brands.builder()
                .id(dto.getId())
                .name(dto.getName())
                .slug(dto.getSlug())
                .description(dto.getDescription())
                .status(BrandStatus.ACTIVE)
                .createdByUser(user)
                .updatedByUser(user)
                .imageUrl(dto.getImageUrl())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }

    public static BrandDto toDto(Brands entity) {
        if (entity == null) return null;

        BrandDto dto = new BrandDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setSlug(entity.getSlug());
        dto.setDescription(entity.getDescription());
        dto.setStatus(entity.getStatus());
        dto.setCreatedByUserId(entity.getCreatedByUser() != null ? entity.getCreatedByUser().getId() : null);
        dto.setUpdatedByUserId(entity.getUpdatedByUser() != null ? entity.getUpdatedByUser().getId() : null);
        dto.setImageUrl(entity.getImageUrl());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        return dto;
    }
}
