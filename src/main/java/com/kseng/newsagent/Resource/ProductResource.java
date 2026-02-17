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

/**
 * REST API controller for managing product resources.
 * 
 * Provides HTTP endpoints for CRUD (Create, Read, Update, Delete) operations on products.
 * All endpoints return JSON responses with appropriate HTTP status codes and error messages.
 * The base path for all endpoints is "/products".
 * 
 * Supported operations:
 * - GET /products - retrieve all products
 * - GET /products/{id} - retrieve a specific product by ID
 * - POST /products - create a new product
 * - PUT /products/{id} - update an existing product
 * - DELETE /products/{id} - delete a specific product
 * - DELETE /products - delete all products
 */
@RestController
@RequestMapping("/products")
public class ProductResource {

    @Autowired
    private ProductRepository productRepository;

    /**
     * Retrieves all products in the database.
     * 
     * @return ResponseEntity containing a list of all products with HTTP 200 (OK) status
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productRepository.findAll());
    }

    /**
     * Retrieves a specific product by its ID.
     * 
     * @param id the unique identifier of the product to retrieve
     * @return ResponseEntity containing the Product if found (HTTP 200), or an error message
     *         with HTTP 400 (Bad Request) if the product is not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        Optional<Product> product = productRepository.findById(id);

        if (product.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Product not found with ID: " + id));
        } else {
            return ResponseEntity.ok(product.get());
        }
    }

    /**
     * Creates a new product in the database.
     * 
     * Validates all input fields before creation:
     * - Product name must be provided and non-empty
     * - Price must be provided and non-negative
     * - Quantity must be provided and non-negative
     * - Product name must be unique (no duplicates)
     * 
     * @param product the Product object containing the details to create
     * @return ResponseEntity containing the created Product with HTTP 201 (Created) status,
     *         or an error response with appropriate HTTP status code if validation fails
     */
    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody Product product) {
        // Validate that a product name is provided and not empty or whitespace-only
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Product name is required and cannot be empty"));
        }
        
        // Validate that a product price is provided and is not negative
        if (product.getPrice() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Product price is required"));
        }
        if (product.getPrice() < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Product price cannot be negative"));
        }
        
        // Validate that an initial inventory quantity is provided and is not negative
        if (product.getQuantity() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Product quantity is required"));
        }
        if (product.getQuantity() < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Product quantity cannot be negative"));
        }
        
        // Check for existing products with the same name to prevent duplicates
        Product existingProduct = productRepository.findByName(product.getName());
        if (existingProduct != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "A product with name '" + product.getName() + "' already exists"));
        }
        
        // Persist the new product to the database
        Product newProduct = productRepository.save(product);
        
        // Verify that the product was successfully saved (ID should have been generated)
        if (newProduct.getId() == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create product"));
        }
        
        return ResponseEntity.status(HttpStatus.CREATED).body(newProduct);
    }

    /**
     * Updates an existing product with new details.
     * 
     * All fields of the product are replaced with values from the request body.
     * Does not perform validation on the updated values.
     * 
     * @param id the unique identifier of the product to update
     * @param productDetails the Product object containing the new values
     * @return ResponseEntity containing the updated Product with HTTP 200 (OK) status,
     *         or HTTP 404 (Not Found) if the product does not exist
     */
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

	/**
	 * Deletes a specific product from the database.
	 * 
	 * @param id the unique identifier of the product to delete
	 * @return ResponseEntity with a success message and HTTP 200 (OK) status,
	 *         or HTTP 404 (Not Found) if the product does not exist
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
		if (!productRepository.existsById(id)) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Product not found with ID: " + id));
		}

		productRepository.deleteById(id);
		return ResponseEntity.ok(Map.of("message", "Product deleted successfully"));
	}

	/**
	 * Deletes ALL products from the database in a single operation.
	 * 
	 * WARNING: This is a destructive operation that removes all product records.
	 * Consider implementing additional protection such as:
	 * - Requiring confirmation headers or query parameters
	 * - Limiting access to this endpoint via authentication/authorization
	 * - Adding an audit log for this critical operation
	 * - Implementing a backup/recovery mechanism
	 * 
	 * @return ResponseEntity with a success message and HTTP 200 (OK) status
	 */
	@DeleteMapping
	public ResponseEntity<?> deleteAllProducts() {
		productRepository.deleteAll();
		return ResponseEntity.ok(Map.of("message", "All products deleted successfully"));
	}
}
