package mstm.muasamthongminh.muasamthongminh.modules.reports.controller;

import lombok.RequiredArgsConstructor;
import mstm.muasamthongminh.muasamthongminh.modules.reports.service.ReportExportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports/export")
@RequiredArgsConstructor
public class ReportExportController {

    private final ReportExportService reportExportService;

    /**
     * Xuất báo cáo doanh thu toàn hệ thống ra Excel
     * GET /api/reports/export/revenue/excel?from=2025-09-01&to=2025-09-23
     */
    @GetMapping("/revenue/excel")
    public ResponseEntity<byte[]> exportRevenueExcel(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) throws IOException {
        if (from == null) from = LocalDate.now().withDayOfMonth(1);
        if (to == null) to = LocalDate.now();

        ByteArrayInputStream in = reportExportService.exportRevenueToExcel(from, to);
        byte[] bytes = in.readAllBytes();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=Revenue_Report_" + from + "_to_" + to + ".xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(bytes);
    }

    /**
     * Xuất báo cáo doanh thu toàn hệ thống ra PDF
     * GET /api/reports/export/revenue/pdf?from=2025-09-01&to=2025-09-23
     */
    @GetMapping("/revenue/pdf")
    public ResponseEntity<byte[]> exportRevenuePdf(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) throws IOException {
        if (from == null) from = LocalDate.now().withDayOfMonth(1);
        if (to == null) to = LocalDate.now();

        ByteArrayInputStream in = reportExportService.exportRevenueToPdf(from, to);
        byte[] bytes = in.readAllBytes();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=Revenue_Report_" + from + "_to_" + to + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(bytes);
    }
}
