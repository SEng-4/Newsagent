package com.kseng.newsagent.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kseng.newsagent.Entity.Product;

/**
 * Repository interface for accessing and manipulating Product entities in the database.
 * 
 * 
 * Standard operations include: save(), findById(), findAll(), delete(), deleteAll(), etc.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    /**
     * Finds all products matching a specific type/category.
     * 
     * @param type the product type to search for (e.g., "NEWSPAPER", "MAGAZINE", "OTHER")
     * @return a list of products matching the specified type
     */
    List<Product> findByType(String type);

    /**
     * Finds a single product by its name.
     * 
     * Assumes product names are unique or returns the first match if duplicates exist.
     * Used primarily to prevent duplicate product creation.
     * 
     * @param name the product name to search for
     * @return the Product with the matching name, or null if not found
     */
    Product findByName(String name);
}
