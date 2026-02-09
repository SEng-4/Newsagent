package com.kseng.newsagent.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kseng.newsagent.Entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // "Type": PRODUCT, NEWSPAPER, whatever...
    List<Product> findByType(String type);

    Product findByName(String name);
}
