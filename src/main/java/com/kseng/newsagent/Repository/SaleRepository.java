package com.kseng.newsagent.Repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kseng.newsagent.Entity.Sale;

/**
 * Repository interface for accessing and manipulating Product entities in the database.
 * 
 * 
 * Standard operations include: save(), findById(), findAll(), delete(), deleteAll(), etc.
 */
@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    
    /**
     * Finds all products matching a specific type/category.
     * 
     * @param type the product type to search for (e.g., "NEWSPAPER", "MAGAZINE", "OTHER")
     * @return a list of products matching the specified type
     */
    List<Sale> findByDate(LocalDate date);
}
