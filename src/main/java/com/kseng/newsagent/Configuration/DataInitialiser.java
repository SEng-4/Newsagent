package com.kseng.newsagent.Configuration;

import com.kseng.newsagent.Entity.Product;
import com.kseng.newsagent.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitialiser implements CommandLineRunner {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception {
        // Add dummy data to the database
        productRepository.save(new Product("Daily Times", "NEWSPAPER", 1.50, 100));
        productRepository.save(new Product("Tech Monthly", "MAGAZINE", 5.99, 50));
        productRepository.save(new Product("Gourmet Weekly", "MAGAZINE", 3.99, 30));
        productRepository.save(new Product("Sunday Herald", "NEWSPAPER", 2.00, 80));
        productRepository.save(new Product("Stationery Set", "OTHER", 12.49, 20));
        productRepository.save(new Product("Novel - The Great Adventure", "OTHER", 15.99, 10));
        productRepository.save(new Product("Fashion Forward", "MAGAZINE", 4.50, 40));
        productRepository.save(new Product("Morning News", "NEWSPAPER", 1.20, 120));
        productRepository.save(new Product("Art Supplies Kit", "OTHER", 25.00, 15));
        productRepository.save(new Product("Science Weekly", "MAGAZINE", 6.50, 25));
    }
}
