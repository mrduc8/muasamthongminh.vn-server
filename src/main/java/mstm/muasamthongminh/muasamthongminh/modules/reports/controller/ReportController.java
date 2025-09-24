package mstm.muasamthongminh.muasamthongminh.modules.reports.controller;

import lombok.RequiredArgsConstructor;
import mstm.muasamthongminh.muasamthongminh.modules.auth.security.CustomUserDetails;
import mstm.muasamthongminh.muasamthongminh.modules.reports.dto.LowStockProductResponse;
import mstm.muasamthongminh.muasamthongminh.modules.reports.dto.RevenueReportResponse;
import mstm.muasamthongminh.muasamthongminh.modules.reports.dto.TopSellingProductResponse;
import mstm.muasamthongminh.muasamthongminh.modules.reports.service.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @GetMapping("/revenue")
    public ResponseEntity<List<RevenueReportResponse>> getRevenue(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUser().getId();

        return ResponseEntity.ok(reportService.getRevenueByDayForShop(userId));
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

    @GetMapping("/system/revenue")
    public List<RevenueReportResponse> getSystemRevenue(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        if (from == null) {
            from = LocalDate.now().withDayOfMonth(1); // đầu tháng
        }
        if (to == null) {
            to = LocalDate.now(); // hôm nay
        }

        return reportService.getRevenueByDayForSystemBetween(from, to);
    }

    /**
     * GET /api/reports/system/low-stock?threshold=5
     * → Trả về danh sách sản phẩm toàn hệ thống sắp hết hàng
     */
    @GetMapping("/system/low-stock")
    public List<LowStockProductResponse> getLowStockProducts(
            @RequestParam(defaultValue = "5") int threshold
    ) {
        return reportService.getLowStockProductsForSystem(threshold);
    }

    /**
     * GET /api/reports/system/top-selling?limit=10
     * → Trả về top sản phẩm bán chạy toàn hệ thống
     */
    @GetMapping("/system/top-selling")
    public List<TopSellingProductResponse> getTopSellingProducts(
            @RequestParam(defaultValue = "10") int limit
    ) {
        return reportService.getTopSellingProductsForSystem(limit);
    }


}
