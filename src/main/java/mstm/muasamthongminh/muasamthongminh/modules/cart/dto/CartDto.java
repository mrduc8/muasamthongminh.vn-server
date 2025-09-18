package mstm.muasamthongminh.muasamthongminh.modules.cart.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor
@Data
@Builder
public class CartDto {
    private Long id;

    private Long userId;
    private String sessionId;
    private String status;
    private String currency;

    private BigDecimal subtotal;
    private BigDecimal discountTotal;
    private BigDecimal shippingTotal;
    private BigDecimal taxTotal;
    private BigDecimal grandTotal;

    private List<CartItemDto> items;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
