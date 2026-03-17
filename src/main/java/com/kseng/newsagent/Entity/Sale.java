package com.kseng.newsagent.Entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;

/**
 * Represents a product in the  system.
 * 
 * This JPA entity maps to the 'products' database table and represents various types of
 * items available for sale, including newspapers, magazines, books, and other retail goods.
 * Each product maintains core information needed for inventory management and sales operations.
 * 
 * The entity uses auto-generated primary keys via the IDENTITY strategy, allowing the
 * database to manage unique ID assignment for each product.
 */
@Entity
@Table(name="sales")
public class Sale {

    /** Unique identifier for the product, generated automatically by the database */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private List<Product> sales;

    /** Product category/type (e.g., "NEWSPAPER", "MAGAZINE", "OTHER") */
    @Column
    private LocalDateTime time;

    /** Default no-argument constructor required by JPA */
    public Sale() {}

    /**
     * Constructs a new Product with specified details.
     * 
     * @param name the product name/title
     * @param type the product category or type
     * @param price the retail price
     * @param quantity the initial quantity in stock
     */
    public Sale(List<Product> sales, LocalDateTime time) {
        this.sales = sales;
        this.time = time;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Product> getSales() {
        return sales;
    }

    public void setSales(List<Product> sales) {
        this.sales = sales;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}