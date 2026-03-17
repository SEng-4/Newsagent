package com.kseng.newsagent.Resource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kseng.newsagent.Entity.Product;
import com.kseng.newsagent.Entity.Sale;
import com.kseng.newsagent.Entity.SaleItem;
import com.kseng.newsagent.Repository.ProductRepository;
import com.kseng.newsagent.Repository.SaleRepository;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/sales")
public class SaleResource {

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping
    public ResponseEntity<List<Sale>> getAllSales() {
        return ResponseEntity.ok(saleRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSaleById(@PathVariable Long id) {
        return saleRepository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Sale not found with ID: " + id)));
    }

    @Transactional
    @PostMapping
    public ResponseEntity<?> createSale(@RequestBody SaleRequest request) {
        String validationError = validateRequest(request);
        if (validationError != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", validationError));
        }

        List<SaleItem> saleItems = new ArrayList<>();
        for (SaleItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId()).orElse(null);
            if (product == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Product not found with ID: " + itemRequest.getProductId()));
            }
            if (product.getQuantity() < itemRequest.getQuantity()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Insufficient stock for product ID: " + itemRequest.getProductId()));
            }
            product.setQuantity(product.getQuantity() - itemRequest.getQuantity());
            productRepository.save(product);
            saleItems.add(new SaleItem(product, itemRequest.getQuantity()));
        }

        Sale sale = new Sale();
        sale.setItems(saleItems);
        sale.setTime(request.getTime() == null ? LocalDateTime.now() : request.getTime());

        Sale createdSale = saleRepository.save(sale);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSale);
    }

    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSale(@PathVariable Long id, @RequestBody SaleRequest request) {
        Sale existing = saleRepository.findById(id).orElse(null);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Sale not found with ID: " + id));
        }

        String validationError = validateRequest(request);
        if (validationError != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", validationError));
        }

        // Restore stock for the old items
        for (SaleItem oldItem : existing.getItems()) {
            Product product = oldItem.getProduct();
            product.setQuantity(product.getQuantity() + oldItem.getQuantity());
            productRepository.save(product);
        }

        // Deduct stock for the new items
        List<SaleItem> saleItems = new ArrayList<>();
        for (SaleItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId()).orElse(null);
            if (product == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Product not found with ID: " + itemRequest.getProductId()));
            }
            if (product.getQuantity() < itemRequest.getQuantity()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Insufficient stock for product ID: " + itemRequest.getProductId()));
            }
            product.setQuantity(product.getQuantity() - itemRequest.getQuantity());
            productRepository.save(product);
            saleItems.add(new SaleItem(product, itemRequest.getQuantity()));
        }

        existing.setItems(saleItems);
        existing.setTime(request.getTime() == null ? existing.getTime() : request.getTime());

        Sale updatedSale = saleRepository.save(existing);
        return ResponseEntity.ok(updatedSale);
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSale(@PathVariable Long id) {
        Sale sale = saleRepository.findById(id).orElse(null);
        if (sale == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Sale not found with ID: " + id));
        }

        // Restore stock for all items in the deleted sale
        for (SaleItem item : sale.getItems()) {
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity() + item.getQuantity());
            productRepository.save(product);
        }

        saleRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Sale deleted successfully"));
    }

    private String validateRequest(SaleRequest request) {
        if (request == null) {
            return "Sale request body is required";
        }

        if (request.getItems() == null || request.getItems().isEmpty()) {
            return "Sale must contain at least one item";
        }

        for (SaleItemRequest item : request.getItems()) {
            if (item.getProductId() == null) {
                return "Each sale item must include a productId";
            }

            if (item.getQuantity() == null || item.getQuantity() < 1) {
                return "Each sale item quantity must be at least 1";
            }
        }

        return null;
    }

    public static class SaleRequest {
        private List<SaleItemRequest> items;
        private LocalDateTime time;

        public SaleRequest() {
        }

        public List<SaleItemRequest> getItems() {
            return items;
        }

        public void setItems(List<SaleItemRequest> items) {
            this.items = items;
        }

        public LocalDateTime getTime() {
            return time;
        }

        public void setTime(LocalDateTime time) {
            this.time = time;
        }
    }

    public static class SaleItemRequest {
        private Long productId;
        private Integer quantity;

        public SaleItemRequest() {
        }

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }
}
