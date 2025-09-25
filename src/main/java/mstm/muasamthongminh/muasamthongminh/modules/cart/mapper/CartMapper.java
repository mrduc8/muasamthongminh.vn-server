package mstm.muasamthongminh.muasamthongminh.modules.cart.mapper;

import mstm.muasamthongminh.muasamthongminh.modules.cart.dto.CartDto;
import mstm.muasamthongminh.muasamthongminh.modules.cart.dto.CartItemDto;
import mstm.muasamthongminh.muasamthongminh.modules.cart.dto.ShopCartDto;
import mstm.muasamthongminh.muasamthongminh.modules.cart.model.Cart;
import mstm.muasamthongminh.muasamthongminh.modules.cart.model.CartItem;
import mstm.muasamthongminh.muasamthongminh.modules.shop.model.Shop;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class CartMapper {
    public static CartDto toDto(Cart cart, List<CartItem> items, Map<Long, Shop> shopMap) {
        CartDto dto = new CartDto();

        dto.setId(cart.getId());
        dto.setUserId(cart.getUserId());
        dto.setSessionId(cart.getSessionId());
        dto.setStatus(cart.getStatus());
        dto.setCurrency(cart.getCurrency());
        dto.setSubtotal(cart.getSubtotal());
        dto.setDiscountTotal(cart.getDiscountTotal());
        dto.setShippingTotal(cart.getShippingTotal());
        dto.setTaxTotal(cart.getTaxTotal());
        dto.setGrandTotal(cart.getGrandTotal());
        dto.setCreatedAt(cart.getCreatedAt());
        dto.setUpdatedAt(cart.getUpdatedAt());

        Map<Long, List<CartItem>> grouped = items.stream().filter(i -> i.getShopId() != null)
                .collect(Collectors.groupingBy(CartItem::getShopId));

        List<ShopCartDto> shopDtos = grouped.entrySet().stream()
                        .map(entry -> {
                            Long shopId = entry.getKey();
                            Shop shop = shopMap.get(shopId);

                            List<CartItemDto> itemDtos = entry.getValue().stream()
                                    .map(CartMapper::toItemDto).toList();

                            return ShopCartDto.builder()
                                    .shopId(shopId)
                                    .shopName(shop != null ? shop.getShopName() : "Unknown")
                                    .shopLogoUrl(shop != null ? shop.getLogoUrl() : null)
                                    .shopBannerUrl(shop != null ? shop.getBannerUrl() : null)
                                    .items(itemDtos)
                                    .build();
                        }).toList();

        dto.setShops(shopDtos);
        return dto;
    }

    private static CartItemDto toItemDto(CartItem i) {
        return CartItemDto.builder()
                .id(i.getId())
                .shopId(i.getShopId())
                .productId(i.getProductId())
                .productVariantId(i.getProductVariantId())
                .quantity(i.getQuantity())
                .unitPrice(i.getUnitPrice())
                .lineTotal(i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .nameSnapshot(i.getNameSnapshot())
                .imageUrlSnapshot(i.getImageUrlSnapshot())
                .variantLabelSnapshot(i.getVariantLabelSnapshot())
                .build();
    }
}
