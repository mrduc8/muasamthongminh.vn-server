package mstm.muasamthongminh.muasamthongminh.modules.shop.mapper;

import mstm.muasamthongminh.muasamthongminh.common.enums.ShopStatus;
import mstm.muasamthongminh.muasamthongminh.modules.auth.model.User;
import mstm.muasamthongminh.muasamthongminh.modules.shop.dto.ShopDto;
import mstm.muasamthongminh.muasamthongminh.modules.shop.model.Shop;
import mstm.muasamthongminh.muasamthongminh.modules.shoprequest.dto.ShopRequestsDto;
import mstm.muasamthongminh.muasamthongminh.modules.shoprequest.model.ShopRequests;

public class ShopMapper {
    public static Shop toEntity(ShopDto dto, User user, ShopRequests shopRequests) {
        if (dto == null) return null;

        return Shop.builder()
                .user(user)
                .shopRequests(shopRequests)
                .shopName(dto.getShopName())
                .description(dto.getDescription())
                .logoUrl(dto.getLogoUrl())
                .bannerUrl(dto.getBannerUrl())
                .address(dto.getAddress())
                .provinceCode(dto.getProvinceCode())
                .districtCode(dto.getDistrictCode())
                .wardCode(dto.getWardCode())
                .status(ShopStatus.PENDING)
                .createAt(dto.getCreateAt())
                .build();
    }

    public static ShopDto toDto(Shop shop) {
        if (shop == null) return null;

        ShopDto dto = new ShopDto();
        dto.setId(shop.getId());
        dto.setUserId(shop.getUser() != null ? shop.getUser().getId() : null);
        dto.setShopRequestsId(shop.getShopRequests() != null ? shop.getShopRequests().getId() : null);
        dto.setShopName(shop.getShopName());
        dto.setDescription(shop.getDescription());
        dto.setLogoUrl(shop.getLogoUrl());
        dto.setBannerUrl(shop.getBannerUrl());
        dto.setAddress(shop.getAddress());
        dto.setProvinceCode(shop.getProvinceCode());
        dto.setDistrictCode(shop.getDistrictCode());
        dto.setWardCode(shop.getWardCode());
        dto.setStatus(shop.getStatus());
        dto.setCreateAt(shop.getCreateAt());
        return dto;
    }

}
