package mstm.muasamthongminh.muasamthongminh.modules.reports.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TopSellingProductResponse {
    private Long productId;
    private String productName;
    private Long totalSold;
}
