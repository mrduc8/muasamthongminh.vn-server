package mstm.muasamthongminh.muasamthongminh.modules.products.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.api.client.util.DateTime;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PriceHistoryDto {
    private Long id;
    private Long productVariantId;
    private Long shopId;
    private BigDecimal oldPrice;
    private BigDecimal newPrice;
    private DateTime changeDate;
    private String reason;
}
