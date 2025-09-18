package mstm.muasamthongminh.muasamthongminh.modules.reports.service;

import lombok.RequiredArgsConstructor;
import mstm.muasamthongminh.muasamthongminh.modules.reports.dto.LowStockProductResponse;
import mstm.muasamthongminh.muasamthongminh.modules.reports.dto.RevenueReportResponse;
import mstm.muasamthongminh.muasamthongminh.modules.reports.dto.TopSellingProductResponse;
import mstm.muasamthongminh.muasamthongminh.modules.reports.repository.ReportRepository;
import mstm.muasamthongminh.muasamthongminh.modules.shop.model.Shop;
import mstm.muasamthongminh.muasamthongminh.modules.shop.repository.ShopRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final ShopRepository shopRepository;

    public List<RevenueReportResponse> getRevenueByDayForShop(Long userId) {
        Shop shop = shopRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("User chưa có shop"));

        return reportRepository.getRevenueByDayForShop(shop.getId()).stream()
                .map(r -> new RevenueReportResponse(
                        r[0].toString(),
                        ((Number) r[1]).longValue()
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
}

