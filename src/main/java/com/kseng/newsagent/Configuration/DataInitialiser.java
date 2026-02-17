package com.kseng.newsagent.Configuration;

import com.kseng.newsagent.Entity.Product;
import com.kseng.newsagent.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Initializes the application with sample product data on startup.
 * 
 * This component implements CommandLineRunner to execute initialization logic after
 * the Spring application context has been fully loaded. It populates the database with
 * a diverse set of sample products across different categories (newspapers, magazines,
 * and other items) to facilitate testing and demonstration of the API.
 * 
 * This data is added each time the application starts. For production use, consider
 * implementing conditional logic to avoid re-adding data on every startup.
 */
@Component
public class DataInitialiser implements CommandLineRunner {

    @Autowired
    private ProductRepository productRepository;

    /**
     * Runs on application startup to populate the database with sample product data.
     * Creates 10 sample products with varying names, categories, prices, and quantities.
     * 
     * @param args command-line arguments (not used in this implementation)
     * @throws Exception if an error occurs during data initialization
     */
    @Override
    public void run(String... args) throws Exception {
        // Populate database with sample products representing different product types
        // Products include newspapers, magazines, and miscellaneous items with realistic pricing
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
