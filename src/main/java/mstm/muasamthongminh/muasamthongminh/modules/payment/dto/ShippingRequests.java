package mstm.muasamthongminh.muasamthongminh.modules.payment.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Data;
import mstm.muasamthongminh.muasamthongminh.modules.payment.enums.ShippingStatus;
import mstm.muasamthongminh.muasamthongminh.modules.payment.model.Orders;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShippingRequests {
    private Long id;
    private Long orderId;

    private ShippingStatus shippingStatus;
    private BigDecimal shippingFee;
    private String trackingNumber;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
