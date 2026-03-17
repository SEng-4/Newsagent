package com.kseng.newsagent.Repository;

import java.time.LocalDateTime;
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
     * Finds all sales between two date/time values (inclusive).
     *
     * @param startTime start of the date/time range
     * @param endTime end of the date/time range
     * @return a list of sales in the supplied range
     */
    List<Sale> findByTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
}
