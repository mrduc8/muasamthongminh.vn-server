package mstm.muasamthongminh.muasamthongminh.modules.reports.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RevenueReportResponse {
    private String date;
    private BigDecimal totalRevenue;
}
