package com.bookstore.reporting.controller;

import com.bookstore.dto.auth.UserPurchaseStatsDTO;
import com.bookstore.dto.book.BookInventoryDTO;
import com.bookstore.reporting.util.PdfReportUtil;
import com.bookstore.reporting.service.ReportService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

	@Autowired
    private ReportService reportService;
    
    @Autowired
    private PdfReportUtil pdfReportUtil;
    

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/sales")
    public ResponseEntity<Map<String, Map<String, BigDecimal>>> getSalesReport() {
        return ResponseEntity.ok(reportService.getAllSalesTrends());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/sales/pdf")
    public ResponseEntity<byte[]> exportSalesPdf() {
    	Map<String, Map<String, BigDecimal>> report = reportService.getAllSalesTrends();
        byte[] pdf = pdfReportUtil.generateSalesReportPdf(report);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=sales_report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    
    
    
    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/inventory")
    public ResponseEntity<Map<String, List<BookInventoryDTO>>> getInventoryReport() {
        return ResponseEntity.ok(reportService.getInventoryReport());
    }
    
//    @PreAuthorize("hasRole('ADMIN')")
//    @GetMapping("/inventory/pdf")
//    public ResponseEntity<byte[]> exportInventoryPdf() {
//    	Map<String, List<BookInventoryDTO>> report = reportService.getInventoryReport();
//        byte[] pdf = pdfReportUtil.generateInventoryReportPdf(report);
//
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=inventory_report.pdf")
//                .contentType(MediaType.APPLICATION_PDF)
//                .body(pdf);
//    }

    
    
    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/customer-behavior")
    public ResponseEntity<Map<String, Object>> getCustomerBehaviorDashboard() {
        return ResponseEntity.ok(reportService.getCustomerBehaviorDashboard());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/customer-insights/pdf")
    public ResponseEntity<byte[]> exportCustomerInsightsPdf() {
        Map<String, Object> behavior = reportService.getCustomerBehaviorReport();
        List<UserPurchaseStatsDTO> mostActive = reportService.getMostActiveUsers();
        Map<String, Object> trends = reportService.getAllPurchaseTrends();

        byte[] pdf = pdfReportUtil.generateCustomerInsightsPdf(behavior, mostActive, trends);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=customer_insights.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    
    

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/revenue")
    public ResponseEntity<Map<String, Object>> getRevenueAnalytics() {
        return ResponseEntity.ok(reportService.getRevenueAnalytics());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/revenue/pdf")
    public ResponseEntity<byte[]> exportRevenuePdf() {
        Map<String, Object> report = reportService.getRevenueAnalytics();
        byte[] pdf = pdfReportUtil.generateRevenueReportPdf(report);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=revenue_report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }


}
