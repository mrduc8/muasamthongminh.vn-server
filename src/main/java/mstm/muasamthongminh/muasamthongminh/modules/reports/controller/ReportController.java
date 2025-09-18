package mstm.muasamthongminh.muasamthongminh.modules.reports.controller;

import lombok.RequiredArgsConstructor;
import mstm.muasamthongminh.muasamthongminh.modules.auth.security.CustomUserDetails;
import mstm.muasamthongminh.muasamthongminh.modules.reports.dto.LowStockProductResponse;
import mstm.muasamthongminh.muasamthongminh.modules.reports.dto.RevenueReportResponse;
import mstm.muasamthongminh.muasamthongminh.modules.reports.dto.TopSellingProductResponse;
import mstm.muasamthongminh.muasamthongminh.modules.reports.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @GetMapping("/revenue")
    public ResponseEntity<List<RevenueReportResponse>> getRevenue(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return ResponseEntity.ok(reportService.getRevenueByDayForShop(userDetails.getUser().getId()));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<LowStockProductResponse>> getLowStock(
            Authentication authentication,
            @RequestParam(defaultValue = "5") int threshold
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return ResponseEntity.ok(reportService.getLowStockProductsForShop(userDetails.getUser().getId(), threshold));
    }

    @GetMapping("/top-selling")
    public ResponseEntity<List<TopSellingProductResponse>> getTopSelling(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return ResponseEntity.ok(reportService.getTopSellingProductsForShop(userDetails.getUser().getId()));
    }
}
