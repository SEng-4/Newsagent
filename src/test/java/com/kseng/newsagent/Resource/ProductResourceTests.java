package com.kseng.newsagent.Resource;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.kseng.newsagent.Entity.Product;
import com.kseng.newsagent.Repository.ProductRepository;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(ProductResource.class)
public class ProductResourceTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductRepository productRepository;

    @Test
    public void testGetAllProducts() throws Exception {
        // Arrange
        Product product1 = new Product("Newspaper A", "NEWSPAPER", 1.5, 100);
        Product product2 = new Product("Magazine B", "MAGAZINE", 3.0, 50);
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));

        // Act & Assert
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Newspaper A"))
                .andExpect(jsonPath("$[1].name").value("Magazine B"));
    }

    @Test
    public void testGetProductById() throws Exception {
        // Arrange
        Product product = new Product("Book C", "BOOK", 10.0, 20);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act & Assert
        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Book C"));
    }

    @Test
    public void testCreateProduct() throws Exception {
        // Arrange
        Product product = new Product("Book D", "BOOK", 15.0, 30);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act & Assert
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Book D"));
    }

    @Test
    public void testUpdateProduct() throws Exception {
        // Arrange
        Product existingProduct = new Product("Book E", "BOOK", 20.0, 10);
        Product updatedProduct = new Product("Updated Book E", "BOOK", 25.0, 15);
        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        // Act & Assert
        mockMvc.perform(put("/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(updatedProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Book E"));
    }

    @Test
    public void testDeleteProduct() throws Exception {
        // Arrange
        when(productRepository.existsById(1L)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Product deleted successfully"));
    }
}