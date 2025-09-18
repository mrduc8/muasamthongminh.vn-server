package mstm.muasamthongminh.muasamthongminh.modules.payment.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import mstm.muasamthongminh.muasamthongminh.modules.payment.enums.OrderStatus;
import mstm.muasamthongminh.muasamthongminh.modules.payment.enums.PaymentMethod;
import mstm.muasamthongminh.muasamthongminh.modules.payment.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderRequest {
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
}
