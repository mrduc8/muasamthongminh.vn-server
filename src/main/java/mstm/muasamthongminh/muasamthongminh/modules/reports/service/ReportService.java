package mstm.muasamthongminh.muasamthongminh.modules.reports.service;

import lombok.RequiredArgsConstructor;
import mstm.muasamthongminh.muasamthongminh.modules.payment.enums.OrderStatus;
import mstm.muasamthongminh.muasamthongminh.modules.payment.enums.PaymentStatus;
import mstm.muasamthongminh.muasamthongminh.modules.payment.model.OrderItem;
import mstm.muasamthongminh.muasamthongminh.modules.payment.model.Orders;
import mstm.muasamthongminh.muasamthongminh.modules.payment.repository.OrderRepository;
import mstm.muasamthongminh.muasamthongminh.modules.reports.dto.LowStockProductResponse;
import mstm.muasamthongminh.muasamthongminh.modules.reports.dto.RevenueReportResponse;
import mstm.muasamthongminh.muasamthongminh.modules.reports.dto.TopSellingProductResponse;
import mstm.muasamthongminh.muasamthongminh.modules.reports.repository.ReportRepository;
import mstm.muasamthongminh.muasamthongminh.modules.shop.model.Shop;
import mstm.muasamthongminh.muasamthongminh.modules.shop.repository.ShopRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final ShopRepository shopRepository;
    private final OrderRepository orderRepository;

    public List<RevenueReportResponse> getRevenueByDayForShop(Long userId) {
        Shop shop = shopRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("User chưa có shop"));

        List<Object[]> results = reportRepository.getRevenueByDayForShop(
                OrderStatus.COMPLETED,
                PaymentStatus.PAID,
                shop.getId()
        );

        return results.stream()
                .map(r -> new RevenueReportResponse(
                        r[0].toString(),
                        (BigDecimal) r[1]
                ))
                .toList();
    }

    public List<LowStockProductResponse> getLowStockProductsForShop(Long userId, int threshold) {
        Shop shop = shopRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("User chưa có shop"));

        return reportRepository.getLowStockProductsForShop(shop.getId(), threshold).stream()
                .map(r -> new LowStockProductResponse(
                        ((Number) r[0]).longValue(),
                        (String) r[1],
                        ((Number) r[2]).intValue()
                ))
                .toList();
    }

    public List<TopSellingProductResponse> getTopSellingProductsForShop(Long userId) {
        Shop shop = shopRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("User chưa có shop"));

        return reportRepository.getTopSellingProductsForShop(shop.getId(), PageRequest.of(0, 10)).stream()
                .map(r -> new TopSellingProductResponse(
                        ((Number) r[0]).longValue(),
                        (String) r[1],
                        ((Number) r[2]).longValue()
                ))
                .toList();
    }

    /**
     * Báo cáo doanh thu toàn hệ thống theo ngày
     */
    public List<RevenueReportResponse> getRevenueByDayForSystem() {
        List<Object[]> results = reportRepository.getRevenueByDayForSystem(
                OrderStatus.COMPLETED,
                PaymentStatus.PAID
        );

        return results.stream()
                .map(r -> new RevenueReportResponse(
                        r[0].toString(),
                        (BigDecimal) r[1]
                ))
                .toList();
    }

    /**
     * Báo cáo doanh thu toàn hệ thống theo ngày, có lọc khoảng thời gian
     */
    public List<RevenueReportResponse> getRevenueByDayForSystemBetween(LocalDate from, LocalDate to) {
        List<Object[]> results = reportRepository.getRevenueByDayForSystemBetween(
                OrderStatus.COMPLETED,
                PaymentStatus.PAID,
                from,
                to
        );

        return results.stream()
                .map(r -> new RevenueReportResponse(
                        r[0].toString(),
                        (BigDecimal) r[1]
                ))
                .toList();
    }


    /**
     * Báo cáo sản phẩm sắp hết hàng toàn hệ thống
     */
    public List<LowStockProductResponse> getLowStockProductsForSystem(int threshold) {
        return reportRepository.getLowStockProductsForSystem(threshold).stream()
                .map(r -> new LowStockProductResponse(
                        ((Number) r[0]).longValue(),
                        (String) r[1],
                        ((Number) r[2]).intValue()
                ))
                .toList();
    }

    /**
     * Báo cáo top sản phẩm bán chạy toàn hệ thống
     */
    public List<TopSellingProductResponse> getTopSellingProductsForSystem(int limit) {
        return reportRepository.getTopSellingProductsForSystem(
                        OrderStatus.COMPLETED,
                        PaymentStatus.PAID,
                        PageRequest.of(0, limit))
                .stream()
                .map(r -> new TopSellingProductResponse(
                        ((Number) r[0]).longValue(),
                        (String) r[1],
                        ((Number) r[2]).longValue()
                ))
                .toList();
    }
}

