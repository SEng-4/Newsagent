package com.kseng.newsagent.Service;

import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.util.Locale;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import com.kseng.newsagent.Entity.Sale;
import com.kseng.newsagent.Entity.SaleItem;

@Service
public class ReceiptService {

    private static final float MARGIN = 50;
    private static final float LINE_HEIGHT = 15;
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(Locale.UK);
    
    public byte[] generateReceiptPdf(Sale sale) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                float yPosition = page.getMediaBox().getHeight() - MARGIN;
                
                // Header
                yPosition = drawHeader(contentStream, document, yPosition);
                
                // Sale details
                yPosition = drawSaleDetails(contentStream, document, sale, yPosition);
                
                // Items table
                yPosition = drawItemsTable(contentStream, document, sale, yPosition);
                
                // Total
                drawTotal(contentStream, document, sale, yPosition);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.save(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF receipt", e);
        }
    }

    private float drawHeader(PDPageContentStream contentStream, PDDocument document, float yPosition) throws Exception {
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 24);
        contentStream.beginText();
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText("RECEIPT");
        contentStream.endText();
        
        return yPosition - 39;
    }

    private float drawSaleDetails(PDPageContentStream contentStream, PDDocument document, Sale sale, float yPosition) throws Exception {
        contentStream.setFont(PDType1Font.HELVETICA, 10);
        
        // Sale ID
        contentStream.beginText();
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText("Sale ID: " + sale.getId());
        contentStream.endText();
        
        yPosition -= LINE_HEIGHT;
        
        // Sale date/time
        contentStream.beginText();
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText("Date: " + sale.getTime());
        contentStream.endText();
        
        return yPosition - 30;
    }

    private float drawItemsTable(PDPageContentStream contentStream, PDDocument document, Sale sale, float yPosition) throws Exception {
        // Table header
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
        contentStream.beginText();
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText("Product");
        contentStream.endText();
        
        contentStream.beginText();
        contentStream.newLineAtOffset(MARGIN + 250, yPosition);
        contentStream.showText("Qty");
        contentStream.endText();
        
        contentStream.beginText();
        contentStream.newLineAtOffset(MARGIN + 290, yPosition);
        contentStream.showText("Unit Price");
        contentStream.endText();
        
        contentStream.beginText();
        contentStream.newLineAtOffset(MARGIN + 380, yPosition);
        contentStream.showText("Total");
        contentStream.endText();
        
        yPosition -= LINE_HEIGHT;
        
        // Draw separator line
        contentStream.setLineWidth(0.5f);
        contentStream.moveTo(MARGIN, yPosition);
        contentStream.lineTo(550, yPosition);
        contentStream.stroke();
        
        yPosition -= LINE_HEIGHT;
        
        // Table data
        contentStream.setFont(PDType1Font.HELVETICA, 10);
        for (SaleItem item : sale.getItems()) {
            String productName = item.getProduct().getName();
            Integer quantity = item.getQuantity();
            Double unitPrice = item.getProduct().getPrice();
            Double lineTotal = unitPrice * quantity;
            
            // Product name
            String displayName = productName.length() > 30 ? productName.substring(0, 27) + "..." : productName; // shorten if to long
            contentStream.beginText();
            contentStream.newLineAtOffset(MARGIN, yPosition);
            contentStream.showText(displayName);
            contentStream.endText();
            
            // Quantity
            contentStream.beginText();
            contentStream.newLineAtOffset(MARGIN + 250, yPosition);
            contentStream.showText(quantity.toString());
            contentStream.endText();
            
            // Unit Price
            contentStream.beginText();
            contentStream.newLineAtOffset(MARGIN + 290, yPosition);
            contentStream.showText(CURRENCY_FORMAT.format(unitPrice));
            contentStream.endText();
            
            // Line Total
            contentStream.beginText();
            contentStream.newLineAtOffset(MARGIN + 380, yPosition);
            contentStream.showText(CURRENCY_FORMAT.format(lineTotal));
            contentStream.endText();
            
            yPosition -= LINE_HEIGHT;
        }
        
        return yPosition;
    }

    private void drawTotal(PDPageContentStream contentStream, PDDocument document, Sale sale, float yPosition) throws Exception {
        // Separator line
        contentStream.setLineWidth(0.5f);
        contentStream.moveTo(MARGIN, yPosition);
        contentStream.lineTo(550, yPosition);
        contentStream.stroke();
        
        yPosition -= LINE_HEIGHT;
        
        // Calculate total
        Double total = sale.getItems().stream()
            .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
            .sum();
        
        // Draw total
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        contentStream.beginText();
        contentStream.newLineAtOffset(MARGIN + 350, yPosition);
        contentStream.showText("Total: " + CURRENCY_FORMAT.format(total));
        contentStream.endText();
    }
}
