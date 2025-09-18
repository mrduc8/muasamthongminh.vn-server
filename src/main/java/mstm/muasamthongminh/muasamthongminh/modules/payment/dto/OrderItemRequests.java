package mstm.muasamthongminh.muasamthongminh.modules.payment.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Data;
import mstm.muasamthongminh.muasamthongminh.modules.payment.model.Orders;
import mstm.muasamthongminh.muasamthongminh.modules.products.model.ProductVariants;
import mstm.muasamthongminh.muasamthongminh.modules.products.model.Products;

import java.math.BigDecimal;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderItemRequests {
    private Long id;
    private Long orderId;
    private Long productId;
    private Long productVariantId;

    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;

    private String nameSnapshot;
    private String imageUrlSnapshot;
    private String variantLabelSnapshot;
}
