package mstm.muasamthongminh.muasamthongminh.modules.cart.dto;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor
@Data
@Builder
public class CartItemDto {
    private Long id;
    private Long shopId;
    private Long productId;
    private Integer productVariantId;
    private Integer quantity;

    private BigDecimal unitPrice;
    private BigDecimal lineTotal;            // unitPrice * quantity (tiện FE)

    private String nameSnapshot;
    private String imageUrlSnapshot;
    private String variantLabelSnapshot;
}
