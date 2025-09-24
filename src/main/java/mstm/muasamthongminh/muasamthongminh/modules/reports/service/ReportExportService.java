package mstm.muasamthongminh.muasamthongminh.modules.reports.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import lombok.RequiredArgsConstructor;
import mstm.muasamthongminh.muasamthongminh.modules.reports.dto.RevenueReportResponse;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.io.font.PdfEncodings;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ReportExportService {

    private final ReportService reportService;

    // Sửa formatter để tránh lỗi ký tự ₫
    private final DecimalFormat decimalFormat = new DecimalFormat("#,###", new DecimalFormatSymbols(new Locale("vi", "VN")));

    private String formatCurrency(BigDecimal amount) {
        return decimalFormat.format(amount) + " VNĐ";
    }

    /**
     * Xuất báo cáo doanh thu toàn hệ thống ra Excel
     */
    public ByteArrayInputStream exportRevenueToExcel(LocalDate from, LocalDate to) throws IOException {
        List<RevenueReportResponse> data = reportService.getRevenueByDayForSystemBetween(from, to);

        Workbook workbook = new XSSFWorkbook();

        /* ==== SHEET 1: SUMMARY ==== */
        Sheet summary = workbook.createSheet("Summary");
        int rowIdx = 0;

        double total = data.stream().mapToDouble(r -> r.getTotalRevenue().doubleValue()).sum();
        RevenueReportResponse maxDay = data.stream().max(Comparator.comparing(RevenueReportResponse::getTotalRevenue)).orElse(null);
        RevenueReportResponse minDay = data.stream().min(Comparator.comparing(RevenueReportResponse::getTotalRevenue)).orElse(null);

        summary.createRow(rowIdx++).createCell(0).setCellValue("BÁO CÁO DOANH THU TOÀN HỆ THỐNG");
        summary.createRow(rowIdx++).createCell(0).setCellValue("Thời gian: " + from + " đến " + to);
        summary.createRow(rowIdx++).createCell(0).setCellValue("Ngày lập báo cáo: " + LocalDate.now());
        summary.createRow(rowIdx++).createCell(0).setCellValue("Tổng doanh thu: " + formatCurrency(BigDecimal.valueOf(total)));

        if (maxDay != null) {
            summary.createRow(rowIdx++).createCell(0)
                    .setCellValue("Ngày cao nhất: " + maxDay.getDate() + " (" + formatCurrency(maxDay.getTotalRevenue()) + ")");
        }
        if (minDay != null) {
            summary.createRow(rowIdx++).createCell(0)
                    .setCellValue("Ngày thấp nhất: " + minDay.getDate() + " (" + formatCurrency(minDay.getTotalRevenue()) + ")");
        }
        summary.createRow(rowIdx).createCell(0).setCellValue("Số ngày có doanh thu: " + data.size());

        /* ==== SHEET 2: REVENUE ==== */
        Sheet sheet = workbook.createSheet("Revenue");
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Ngày");
        header.createCell(1).setCellValue("Doanh thu");

        int row = 1;
        for (RevenueReportResponse r : data) {
            Row dataRow = sheet.createRow(row++);
            dataRow.createCell(0).setCellValue(r.getDate());
            dataRow.createCell(1).setCellValue(r.getTotalRevenue().doubleValue());
        }

        // Dòng tổng cộng
        Row totalRow = sheet.createRow(row);
        totalRow.createCell(0).setCellValue("TỔNG CỘNG");
        totalRow.createCell(1).setCellValue(total);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return new ByteArrayInputStream(out.toByteArray());
    }

    /**
     * Xuất báo cáo doanh thu toàn hệ thống ra PDF
     */
    public ByteArrayInputStream exportRevenueToPdf(LocalDate from, LocalDate to) {
        List<RevenueReportResponse> data = reportService.getRevenueByDayForSystemBetween(from, to);

        double total = data.stream().mapToDouble(r -> r.getTotalRevenue().doubleValue()).sum();
        RevenueReportResponse maxDay = data.stream().max(Comparator.comparing(RevenueReportResponse::getTotalRevenue)).orElse(null);
        RevenueReportResponse minDay = data.stream().min(Comparator.comparing(RevenueReportResponse::getTotalRevenue)).orElse(null);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        try {
            // Load font Times New Roman
            InputStream fontStream = getClass().getResourceAsStream("/fonts/SVN-Times New Roman.ttf");
            PdfFont font = PdfFontFactory.createFont(
                    IOUtils.toByteArray(fontStream),
                    PdfEncodings.IDENTITY_H,
                    PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED
            );
            document.setFont(font);

            /* ==== TIÊU ĐỀ ==== */
            Paragraph title = new Paragraph("BÁO CÁO DOANH THU TOÀN HỆ THỐNG")
                    .setFont(font)
                    .setBold()
                    .setFontSize(16)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(title);

            /* ==== HÀNG THỜI GIAN & NGÀY LẬP ==== */
            Table infoTable = new Table(new float[]{1, 1}).useAllAvailableWidth();
            infoTable.addCell(new Cell().add(new Paragraph("Thời gian: " + from + " đến " + to))
                    .setBorder(Border.NO_BORDER));
            infoTable.addCell(new Cell().add(new Paragraph("Ngày lập báo cáo: " + LocalDate.now()))
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setBorder(Border.NO_BORDER));
            document.add(infoTable.setMarginBottom(15));

            /* ==== TÓM TẮT ==== */
            document.add(new Paragraph("TÓM TẮT KẾT QUẢ")
                    .setBold().setFontSize(13).setMarginBottom(5));
            document.add(new Paragraph("Tổng doanh thu: " + formatCurrency(BigDecimal.valueOf(total))));
            if (maxDay != null) {
                document.add(new Paragraph("Ngày cao nhất: " + maxDay.getDate() +
                        " (" + formatCurrency(maxDay.getTotalRevenue()) + ")"));
            }
            if (minDay != null) {
                document.add(new Paragraph("Ngày thấp nhất: " + minDay.getDate() +
                        " (" + formatCurrency(minDay.getTotalRevenue()) + ")"));
            }
            document.add(new Paragraph("Số ngày có doanh thu: " + data.size())
                    .setMarginBottom(15));

            /* ==== BẢNG CHI TIẾT ==== */
            Table table = new Table(new float[]{1, 1}).useAllAvailableWidth();

            // Header
            table.addHeaderCell(new Cell()
                    .add(new Paragraph("Ngày"))
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER));

            table.addHeaderCell(new Cell()
                    .add(new Paragraph("Doanh thu"))
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER));

            // Dữ liệu
            for (RevenueReportResponse r : data) {
                table.addCell(new Cell()
                        .add(new Paragraph(r.getDate()))
                        .setTextAlignment(TextAlignment.CENTER));

                table.addCell(new Cell()
                        .add(new Paragraph(formatCurrency(r.getTotalRevenue())))
                        .setTextAlignment(TextAlignment.CENTER));
            }

            // Dòng tổng cộng
            table.addCell(new Cell()
                    .add(new Paragraph("TỔNG CỘNG"))
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER));

            table.addCell(new Cell()
                    .add(new Paragraph(formatCurrency(BigDecimal.valueOf(total))))
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(table);

        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi load font", e);
        } finally {
            document.close();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
