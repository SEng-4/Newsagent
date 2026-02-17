package com.kseng.newsagent.Entity;

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
@Table(name="products")
public class Product {

    /** Unique identifier for the product, generated automatically by the database */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Product name/title  */
    @Column
    private String name;

    /** Product category/type (e.g., "NEWSPAPER", "MAGAZINE", "OTHER") */
    @Column
    private String type;

    /** Retail price of the product in currency units (e.g., 1.50 for €1.50) */
    @Column
    private Double price;

    /** Current inventory quantity available for sale */
    @Column
    private Integer quantity;

    /** Default no-argument constructor required by JPA */
    public Product() {}

    /**
     * Constructs a new Product with specified details.
     * 
     * @param name the product name/title
     * @param type the product category or type
     * @param price the retail price
     * @param quantity the initial quantity in stock
     */
    public Product(String name, String type, Double price, Integer quantity) {
        this.name = name;
        this.type = type;
        this.price = price;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Product [id=" + id + ", name=" + name + ", type=" + type + ", price=" + price + ", quantity=" + quantity
                + "]";
    }
}
