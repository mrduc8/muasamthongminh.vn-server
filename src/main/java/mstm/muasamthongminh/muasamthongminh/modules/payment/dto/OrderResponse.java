package mstm.muasamthongminh.muasamthongminh.modules.payment.dto;

import lombok.Builder;
import lombok.Data;
import mstm.muasamthongminh.muasamthongminh.modules.payment.enums.OrderStatus;
import mstm.muasamthongminh.muasamthongminh.modules.payment.enums.PaymentMethod;
import mstm.muasamthongminh.muasamthongminh.modules.payment.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private Long id;
    private Long userId;
    private Long addressId;

    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private OrderStatus orderStatus;

    private BigDecimal subtotal;
    private BigDecimal shippingFee;
    private BigDecimal discount;
    private BigDecimal grandTotal;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<OrderItemRequests> items;
    private ShippingRequests shipping;
}
