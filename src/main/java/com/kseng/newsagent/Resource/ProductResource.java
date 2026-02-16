package com.kseng.newsagent.Resource;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.kseng.newsagent.Entity.Product;
import com.kseng.newsagent.Repository.ProductRepository;

@RestController
@RequestMapping("/products")
public class ProductResource {

    @Autowired
    private ProductRepository productRepository;

    // GET /products - return all products
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productRepository.findAll());
    }

    // GET /products/{id} - return specific product
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        Optional<Product> product = productRepository.findById(id);

        if (product.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Product not found with ID: " + id));
        } else {
            return ResponseEntity.ok(product.get());
        }
    }

    // POST /products - create new product
    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody Product product) {
        // Verify that a product name is provided and valid
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Product name is required and cannot be empty"));
        }
        
        // Verify that a product price is provided as a valid, non-negative double
        if (product.getPrice() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Product price is required"));
        }
        if (product.getPrice() < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Product price cannot be negative"));
        }
        
        // Verify that an initial inventory value is provided and cannot be less than zero
        if (product.getQuantity() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Product quantity is required"));
        }
        if (product.getQuantity() < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Product quantity cannot be negative"));
        }
        
        // Verify no duplicates of the product (by name) are being created
        Product existingProduct = productRepository.findByName(product.getName());
        if (existingProduct != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "A product with name '" + product.getName() + "' already exists"));
        }
        
        // Save and verify that it was actually added
        Product newProduct = productRepository.save(product);
        
        // Verify that it was actually saved (ID should be generated)
        if (newProduct.getId() == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create product"));
        }
        
        return ResponseEntity.status(HttpStatus.CREATED).body(newProduct);
    }

    // PUT /products/{id} - update existing product
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        return productRepository.findById(id).<ResponseEntity<?>>map(product -> {
            product.setName(productDetails.getName());
            product.setPrice(productDetails.getPrice());
            product.setType(productDetails.getType());
            product.setQuantity(productDetails.getQuantity());

            Product updatedProduct = productRepository.save(product);
            return ResponseEntity.ok(updatedProduct);
            
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Product not found with ID: " + id)));
    }

	// DELETE /tasks/{id} - delete specific task
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
		if (!productRepository.existsById(id)) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Product not found with ID: " + id));
		}

		productRepository.deleteById(id);
		return ResponseEntity.ok(Map.of("message", "Product deleted successfully"));
	}

	// DELETE /tasks - delete ALL tasks (dangerous)
	// TODO: Add protection against blindly deleting all tasks?
	@DeleteMapping
	public ResponseEntity<?> deleteAllProducts() {
		productRepository.deleteAll();
		return ResponseEntity.ok(Map.of("message", "All products deleted successfully"));
	}
}
