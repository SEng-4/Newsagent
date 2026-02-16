package com.kseng.newsagent.Repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import com.kseng.newsagent.Entity.Product;

@DataJpaTest
public class ProductRepositoryTests {

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void testFindByType() {
        // Arrange
        Product product1 = new Product("Newspaper A", "NEWSPAPER", 1.5, 100);
        Product product2 = new Product("Magazine B", "MAGAZINE", 3.0, 50);
        productRepository.save(product1);
        productRepository.save(product2);

        // Act
        List<Product> newspapers = productRepository.findByType("NEWSPAPER");

        // Assert
        assertThat(newspapers).hasSize(1);
        assertThat(newspapers.get(0).getName()).isEqualTo("Newspaper A");
    }

    @Test
    public void testFindByName() {
        // Arrange
        Product product = new Product("Book C", "BOOK", 10.0, 20);
        productRepository.save(product);

        // Act
        Product foundProduct = productRepository.findByName("Book C");

        // Assert
        assertThat(foundProduct).isNotNull();
        assertThat(foundProduct.getName()).isEqualTo("Book C");
    }
}