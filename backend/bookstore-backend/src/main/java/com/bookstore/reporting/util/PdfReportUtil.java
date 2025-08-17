package com.bookstore.reporting.util;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.bookstore.dto.auth.UserPurchaseStatsDTO;
import com.bookstore.dto.book.BookInventoryDTO;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.element.Cell;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Component
public class PdfReportUtil {

	public byte[] generateSalesReportPdf(Map<String, Map<String, BigDecimal>> data) {
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    PdfWriter writer = new PdfWriter(out);
	    PdfDocument pdf = new PdfDocument(writer);
	    Document document = new Document(pdf);

	    document.add(new Paragraph("Sales Report").setFontSize(18).setBold());
	    document.add(new Paragraph("\n"));

	    for (Map.Entry<String, Map<String, BigDecimal>> entry : data.entrySet()) {
	        String period = entry.getKey();
	        Map<String, BigDecimal> salesData = entry.getValue();

	        document.add(new Paragraph(period.toUpperCase() + " Sales").setFontSize(14).setBold());
	        Table table = new Table(2);
	        table.addHeaderCell(new Cell().add(new Paragraph("Date").setBold()));
	        table.addHeaderCell(new Cell().add(new Paragraph("Revenue").setBold()));

	        for (Map.Entry<String, BigDecimal> row : salesData.entrySet()) {
	            table.addCell(new Cell().add(new Paragraph(row.getKey())));
	            table.addCell(new Cell().add(new Paragraph(row.getValue().toString())));
	        }

	        document.add(table);
	        document.add(new Paragraph("\n"));
	    }

	    document.close();
	    return out.toByteArray();
	}

    
	public byte[] generateInventoryReportPdf(
	        Map<String, List<BookInventoryDTO>> report,
	        Map<String, Map<String, BigDecimal>> customerBehaviorAndTrends) {

	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    PdfWriter writer = new PdfWriter(out);
	    PdfDocument pdf = new PdfDocument(writer);
	    Document document = new Document(pdf);

	    // Title
	    document.add(new Paragraph("Inventory Report").setFontSize(18).setBold());
	    document.add(new Paragraph("\n"));

	    // Inventory Data
	    for (Map.Entry<String, List<BookInventoryDTO>> entry : report.entrySet()) {
	        String category = entry.getKey();
	        List<BookInventoryDTO> books = entry.getValue();

	        document.add(new Paragraph(category.toUpperCase()).setBold().setFontSize(14));
	        Table table = new Table(3);
	        table.addHeaderCell("Title");
	        table.addHeaderCell("Author");
	        table.addHeaderCell("Stock");

	        for (BookInventoryDTO book : books) {
	            table.addCell(book.getTitle());
	            table.addCell(book.getAuthor());
	            table.addCell(String.valueOf(book.getStockQuantity()));
	        }

	        document.add(table);
	        document.add(new Paragraph("\n"));

	        // Customer Behavior & Trends for this category
	        Map<String, BigDecimal> trends = customerBehaviorAndTrends.get(category);
	        if (trends != null && !trends.isEmpty()) {
	            document.add(new Paragraph("Customer Behavior & Purchase Trends")
	                    .setFontSize(13).setBold().setUnderline());
	            Table trendTable = new Table(2);
	            trendTable.addHeaderCell("Trend");
	            trendTable.addHeaderCell("Value");

	            for (Map.Entry<String, BigDecimal> trendEntry : trends.entrySet()) {
	                trendTable.addCell(trendEntry.getKey());
	                trendTable.addCell(String.valueOf(trendEntry.getValue()));
	            }

	            document.add(trendTable);
	            document.add(new Paragraph("\n"));
	        }
	    }

	    document.close();
	    return out.toByteArray();
	}


    
    public byte[] generateCustomerInsightsPdf(Map<String, Object> behavior,
    	    List<UserPurchaseStatsDTO> mostActive,
    	    Map<String, Object> trends
    	) {
    	    ByteArrayOutputStream out = new ByteArrayOutputStream();
    	    PdfWriter writer = new PdfWriter(out);
    	    PdfDocument pdf = new PdfDocument(writer);
    	    Document document = new Document(pdf);

    	    document.add(new Paragraph("Customer Insights Report").setFontSize(18).setBold());
    	    document.add(new Paragraph("\n"));

    	    // 1. Customer Behavior
    	    document.add(new Paragraph("Customer Behavior").setBold().setFontSize(14));
    	    for (Map.Entry<String, Object> entry : behavior.entrySet()) {
    	        document.add(new Paragraph(entry.getKey() + ": " + entry.getValue()));
    	    }

    	    document.add(new Paragraph("\n"));

    	    // 2. Most Active Users
    	    document.add(new Paragraph("Most Active Users").setBold().setFontSize(14));
    	    Table activeUserTable = new Table(3);
    	    activeUserTable.addHeaderCell("Username");
    	    activeUserTable.addHeaderCell("Orders");
    	    activeUserTable.addHeaderCell("Total Spend");

    	    for (UserPurchaseStatsDTO user : mostActive) {
    	        activeUserTable.addCell(user.getEmail());
    	        activeUserTable.addCell(String.valueOf(user.getTotalOrders()));
    	        activeUserTable.addCell(user.getTotalSpent().toString());
    	    }

    	    document.add(activeUserTable);
    	    document.add(new Paragraph("\n"));

    	    // 3. Purchase Trends
    	    document.add(new Paragraph("Purchase Trends").setBold().setFontSize(14));
    	    for (Map.Entry<String, Object> trend : trends.entrySet()) {
    	        document.add(new Paragraph(trend.getKey() + ": " + trend.getValue()));
    	    }

    	    document.close();
    	    return out.toByteArray();
    	}

    public byte[] generateRevenueReportPdf(Map<String, Object> revenueData) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("Revenue Report")
            .setFontSize(18)
            .setBold()
            .setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("\n"));

        for (Map.Entry<String, Object> entry : revenueData.entrySet()) {
            document.add(new Paragraph(entry.getKey())
                .setBold()
                .setFontSize(14)
                .setUnderline());

            Object value = entry.getValue();

            if (value instanceof Map<?, ?> map) {
                Table table = new Table(2);
                table.setWidth(UnitValue.createPercentValue(100));
                table.addHeaderCell(new Cell().add(new Paragraph("Key").setBold()));
                table.addHeaderCell(new Cell().add(new Paragraph("Value").setBold()));

                for (Map.Entry<?, ?> e : map.entrySet()) {
                    String keyStr = String.valueOf(e.getKey());
                    Object valObj = e.getValue();

                    String valStr;
                    if (valObj instanceof BigDecimal decimal) {
                        valStr = decimal.setScale(2, RoundingMode.HALF_UP).toString();
                    } else {
                        valStr = String.valueOf(valObj);
                    }

                    table.addCell(new Cell().add(new Paragraph(keyStr)));
                    table.addCell(new Cell().add(new Paragraph(valStr)));
                }

                document.add(table);
            } else {
                String valStr = (value instanceof BigDecimal bd) 
                    ? bd.setScale(2, RoundingMode.HALF_UP).toString()
                    : String.valueOf(value);

                document.add(new Paragraph(valStr));
            }

            document.add(new Paragraph("\n"));
        }

        document.close();
        return out.toByteArray();
    }



}
