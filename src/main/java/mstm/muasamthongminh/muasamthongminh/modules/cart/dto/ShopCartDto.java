package mstm.muasamthongminh.muasamthongminh.modules.cart.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopCartDto {
    private Long shopId;
    private String shopName;
    private String shopLogoUrl;
    private String shopBannerUrl;
    private List<CartItemDto> items;
}
