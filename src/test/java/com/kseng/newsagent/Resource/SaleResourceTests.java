package com.kseng.newsagent.Resource;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.kseng.newsagent.Entity.Product;
import com.kseng.newsagent.Entity.Sale;
import com.kseng.newsagent.Entity.SaleItem;
import com.kseng.newsagent.Repository.ProductRepository;
import com.kseng.newsagent.Repository.SaleRepository;
import com.kseng.newsagent.Service.ReceiptService;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(SaleResource.class)
public class SaleResourceTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SaleRepository saleRepository;

    @MockitoBean
    private ProductRepository productRepository;

    @MockitoBean
    private ReceiptService receiptService;

    @Test
    public void testGetAllSales() throws Exception {
        // Arrange
        Product product = new Product("Newspaper A", "NEWSPAPER", 1.5, 100);
        product.setId(1L);
        SaleItem saleItem = new SaleItem(product, 2);
        Sale sale = new Sale();
        sale.setId(1L);
        sale.setTime(LocalDateTime.now());
        sale.setItems(Arrays.asList(saleItem));

        when(saleRepository.findAll()).thenReturn(Arrays.asList(sale));

        // Act & Assert
        mockMvc.perform(get("/sales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].items[0].quantity").value(2));
    }

    @Test
    public void testGetSaleById() throws Exception {
        // Arrange
        Product product = new Product("Newspaper A", "NEWSPAPER", 1.5, 100);
        product.setId(1L);
        SaleItem saleItem = new SaleItem(product, 2);
        Sale sale = new Sale();
        sale.setId(1L);
        sale.setTime(LocalDateTime.now());
        sale.setItems(Arrays.asList(saleItem));

        when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));

        // Act & Assert
        mockMvc.perform(get("/sales/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.items[0].quantity").value(2));
    }

    @Test
    public void testGetSaleById_NotFound() throws Exception {
        // Arrange
        when(saleRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/sales/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    public void testGetReceiptPdf_Success() throws Exception {
        // Arrange
        Product product = new Product("Newspaper A", "NEWSPAPER", 1.5, 100);
        product.setId(1L);
        SaleItem saleItem = new SaleItem(product, 2);
        Sale sale = new Sale();
        sale.setId(1L);
        sale.setTime(LocalDateTime.now());
        sale.setItems(Arrays.asList(saleItem));

        byte[] pdfBytes = new byte[] { (byte) 0x25, (byte) 0x50, (byte) 0x44, (byte) 0x46 }; // PDF magic bytes: %PDF
        
        when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));
        when(receiptService.generateReceiptPdf(sale)).thenReturn(pdfBytes);

        // Act & Assert
        mockMvc.perform(get("/sales/1/receipt"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"receipt_1.pdf\""));
    }

    @Test
    public void testGetReceiptPdf_NotFound() throws Exception {
        // Arrange
        when(saleRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/sales/999/receipt"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    public void testCreateSale() throws Exception {
        // Arrange
        Product product = new Product("Newspaper A", "NEWSPAPER", 1.5, 100);
        product.setId(1L);
        
        Sale sale = new Sale();
        sale.setId(1L);
        sale.setTime(LocalDateTime.now());
        SaleItem saleItem = new SaleItem(product, 2);
        sale.setItems(Arrays.asList(saleItem));

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(saleRepository.save(any(Sale.class))).thenReturn(sale);

        SaleResource.SaleItemRequest itemRequest = new SaleResource.SaleItemRequest();
        itemRequest.setProductId(1L);
        itemRequest.setQuantity(2);

        SaleResource.SaleRequest saleRequest = new SaleResource.SaleRequest();
        saleRequest.setItems(Arrays.asList(itemRequest));

        // Act & Assert
        mockMvc.perform(post("/sales")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(saleRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    public void testCreateSale_EmptyItems() throws Exception {
        // Arrange
        SaleResource.SaleRequest saleRequest = new SaleResource.SaleRequest();
        saleRequest.setItems(Arrays.asList());

        // Act & Assert
        mockMvc.perform(post("/sales")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(saleRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    public void testDeleteSale() throws Exception {
        // Arrange
        Product product = new Product("Newspaper A", "NEWSPAPER", 1.5, 100);
        product.setId(1L);
        Sale sale = new Sale();
        sale.setId(1L);
        sale.setTime(LocalDateTime.now());
        SaleItem saleItem = new SaleItem(product, 2);
        sale.setItems(Arrays.asList(saleItem));

        when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));

        // Act & Assert
        mockMvc.perform(delete("/sales/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    public void testDeleteSale_NotFound() throws Exception {
        // Arrange
        when(saleRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(delete("/sales/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }
}
