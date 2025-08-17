package com.bookstore.service;

import com.bookstore.entity.Order;
import com.bookstore.entity.OrderItem;
import com.bookstore.entity.Payment;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class InvoiceService {

    public byte[] generateInvoice(Order order, Payment payment) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Formatters
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

        // Title
        document.add(new Paragraph("Bookstore Invoice")
                .setFontSize(20)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10));

        LineSeparator separator = new LineSeparator(new SolidLine(1f));
        document.add(separator);


        // Order Info
        document.add(new Paragraph("Order ID: " + order.getOrderId()));
        document.add(new Paragraph("Order Date: " + 
            (order.getPlacedAt() != null ? order.getPlacedAt().format(dateFormatter) : "N/A")));
        document.add(new Paragraph("Customer: " + 
            (order.getUser() != null ? order.getUser().getName() : "Unknown")));
        document.add(new Paragraph("Email: " + 
            (order.getUser() != null ? order.getUser().getEmail() : "Unknown")));

        document.add(new Paragraph("\n"));

        // Payment Info
        if (payment != null) {
            document.add(new Paragraph("Payment Method: " + 
                (payment.getMethod() != null ? payment.getMethod().name() : "N/A")));
            document.add(new Paragraph("Transaction ID: " + 
                (payment.getTransactionId() != null ? payment.getTransactionId() : "N/A")));
            document.add(new Paragraph("Payment Status: " + 
                (payment.getStatus() != null ? payment.getStatus().name() : "N/A")));
            document.add(new Paragraph("Payment Date: " + 
                (payment.getPaymentDate() != null ? payment.getPaymentDate().format(dateFormatter) : "N/A")));
        } else {
            document.add(new Paragraph("Payment: Not available"));
        }

        document.add(new Paragraph("\n"));

        // Items Table
        List<OrderItem> items = order.getOrderItems();
        if (items == null || items.isEmpty()) {
            document.add(new Paragraph("No items in this order."));
        } else {
            Table table = new Table(UnitValue.createPercentArray(new float[]{4, 2, 2}))
                    .useAllAvailableWidth();

            table.addHeaderCell(new Cell().add(new Paragraph("Book Title").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Quantity").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Price").setBold()));

            for (OrderItem item : items) {
                String title = (item.getBook() != null && item.getBook().getTitle() != null)
                        ? item.getBook().getTitle() : "Unknown Title";
                table.addCell(title);
                table.addCell(String.valueOf(item.getQuantity()));
                table.addCell("₹" + item.getPrice());
            }

            document.add(table);
        }

        // Total
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("Total Amount: ₹" + order.getTotalAmount())
                .setBold()
                .setTextAlignment(TextAlignment.RIGHT));

        // Footer
        document.add(new Paragraph("\nThank you for shopping with us!")
                .setTextAlignment(TextAlignment.CENTER));

        document.close();
        return out.toByteArray();
    }
}
