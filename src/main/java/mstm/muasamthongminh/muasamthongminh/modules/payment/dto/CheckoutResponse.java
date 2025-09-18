package mstm.muasamthongminh.muasamthongminh.modules.payment.dto;

import lombok.*;
import mstm.muasamthongminh.muasamthongminh.modules.payment.enums.OrderStatus;
import mstm.muasamthongminh.muasamthongminh.modules.payment.enums.PaymentMethod;
import mstm.muasamthongminh.muasamthongminh.modules.payment.enums.PaymentStatus;

import java.math.BigDecimal;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckoutResponse {
    private Long orderId;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private OrderStatus orderStatus;
    private BigDecimal grandTotal;
    private String redirectUrl;
}
