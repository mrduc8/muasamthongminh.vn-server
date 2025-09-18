package mstm.muasamthongminh.muasamthongminh.modules.cart.mapper;

import mstm.muasamthongminh.muasamthongminh.modules.cart.dto.CartDto;
import mstm.muasamthongminh.muasamthongminh.modules.cart.dto.CartItemDto;
import mstm.muasamthongminh.muasamthongminh.modules.cart.model.Cart;
import mstm.muasamthongminh.muasamthongminh.modules.cart.model.CartItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

// CartMapper.java
public final class CartMapper {
    public static CartDto toDto(Cart cart, List<CartItem> items) {
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

        dto.setItems(items.stream().map(CartMapper::toItemDto).toList());
        return dto;
    }

    private static CartItemDto toItemDto(CartItem i) {
        CartItemDto d = new CartItemDto();
        d.setId(i.getId());
        d.setShopId(i.getShopId());
        d.setProductId(i.getProductId());
        d.setProductVariantId(i.getProductVariantId());
        d.setQuantity(i.getQuantity());
        d.setUnitPrice(i.getUnitPrice());
        d.setLineTotal(i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())));
        d.setNameSnapshot(i.getNameSnapshot());
        d.setImageUrlSnapshot(i.getImageUrlSnapshot());
        d.setVariantLabelSnapshot(i.getVariantLabelSnapshot());
        return d;
    }
}
