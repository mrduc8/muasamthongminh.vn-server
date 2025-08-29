package mstm.muasamthongminh.muasamthongminh.modules.products.mapper;

import mstm.muasamthongminh.muasamthongminh.modules.products.dto.PriceHistoryDto;
import mstm.muasamthongminh.muasamthongminh.modules.products.model.PriceHistory;
import mstm.muasamthongminh.muasamthongminh.modules.products.model.ProductVariants;
import mstm.muasamthongminh.muasamthongminh.modules.shop.model.Shop;

public class PriceHistoryMapper {

    public static PriceHistory toEntity(PriceHistoryDto dto, ProductVariants product, Shop shop) {
        if (dto == null) return null;
        if (product == null) return null;
        if (shop == null) return null;

        return PriceHistory.builder()
                .id(dto.getId())
                .productVariantId(product)
                .shopId(shop)
                .oldPrice(dto.getOldPrice())
                .newPrice(dto.getNewPrice())
                .changeDate(dto.getChangeDate())
                .reason(dto.getReason())
                .build();
    }

    public static PriceHistoryDto toDto(PriceHistory entity) {
        if (entity == null) return null;

        PriceHistoryDto dto = new PriceHistoryDto();
        dto.setId(entity.getId());
        dto.setProductVariantId(entity.getProductVariantId() != null ? entity.getProductVariantId().getId() : null);
        dto.setShopId(entity.getShopId() != null ? entity.getShopId().getId() : null);
        dto.setOldPrice(entity.getOldPrice());
        dto.setNewPrice(entity.getNewPrice());
        dto.setChangeDate(entity.getChangeDate());
        dto.setReason(entity.getReason());
        return dto;
    }
}
