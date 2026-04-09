package com.kseng.newsagent.Entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

/** Represents a completed sale transaction with one or more line items. */
@Entity
@Table(name="sales")
public class Sale {

    /** Unique identifier for the sale, generated automatically by the database */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SaleItem> items = new ArrayList<>();

    /** Date/time when the sale was completed. */
    @Column
    private LocalDateTime time;

    /** Default no-argument constructor required by JPA */
    public Sale() {}

    public Sale(List<SaleItem> items, LocalDateTime time) {
        setItems(items);
        this.time = time;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<SaleItem> getItems() {
        return items;
    }

    public void setItems(List<SaleItem> items) {
        this.items.clear();

        if (items == null) {
            return;
        }

        for (SaleItem item : items) {
            addItem(item);
        }
    }

    public void addItem(SaleItem item) {
        if (item == null) {
            return;
        }

        item.setSale(this);
        this.items.add(item);
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}