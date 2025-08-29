package mstm.muasamthongminh.muasamthongminh.modules.shoprequest.mapper;

import mstm.muasamthongminh.muasamthongminh.common.enums.ShopStatus;
import mstm.muasamthongminh.muasamthongminh.modules.auth.model.User;
import mstm.muasamthongminh.muasamthongminh.modules.shoprequest.dto.ShopRequestsDto;
import mstm.muasamthongminh.muasamthongminh.modules.shoprequest.model.ShopRequests;

import java.time.LocalDateTime;

public class ShopRequestMapper {

    public static ShopRequests toEntity(ShopRequestsDto dto, User user) {
        if (dto == null) return null;

        return ShopRequests.builder()
                .user(user)
                .fullName(dto.getFullName())
                .identityNumber(dto.getIdentityNumber())
                .issuedDate(dto.getIssuedDate())
                .expDate(dto.getExpDate())
                .issuedPlace(dto.getIssuedPlace())
                .businessName(dto.getBusinessName())
                .taxCode(dto.getTaxCode())
                .address(dto.getAddress())
                .licenseFileUrl(dto.getLicenseFileUrl())
                .note(dto.getNote())
                .status(ShopStatus.PENDING)
                .createAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static ShopRequestsDto toDto(ShopRequests entity) {
        if (entity == null) return null;

        ShopRequestsDto dto = new ShopRequestsDto();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUser() != null ? entity.getUser().getId() : null);
        dto.setFullName(entity.getFullName());
        dto.setIdentityNumber(entity.getIdentityNumber());
        dto.setIssuedDate(entity.getIssuedDate());
        dto.setExpDate(entity.getExpDate());
        dto.setIssuedPlace(entity.getIssuedPlace());
        dto.setBusinessName(entity.getBusinessName());
        dto.setTaxCode(entity.getTaxCode());
        dto.setAddress(entity.getAddress());
        dto.setLicenseFileUrl(entity.getLicenseFileUrl());
        dto.setNote(entity.getNote());
        dto.setStatus(entity.getStatus());
        dto.setCreatedDate(entity.getCreateAt());
        dto.setUpdatedDate(entity.getUpdatedAt());
        return dto;
    }

    public static ShopRequestsDto toReject(ShopRequests reject) {
        if (reject == null) {
            return null;
        };

        ShopRequestsDto dto = new ShopRequestsDto();
        dto.setId(reject.getId());
        dto.setUserId(reject.getUser() != null ? reject.getUser().getId() : null);
        dto.setFullName(reject.getFullName());
        dto.setNote(reject.getNote());
        return dto;
    }
}
