package com.devsuperior.dscatalog.repositories;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Optional;

import static com.devsuperior.dscatalog.factory.Factory.*;

@DataJpaTest
public class ProductRepositoryTests {

    @Autowired
    private ProductRepository productRepository;

    private long existingId;
    private long nonExistingId;
    private long quantityOfProducts;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 1000L;
        quantityOfProducts = 25L;
    }

    @Test
    public void deleteShouldDeleteObjectWhenIdExists() {

        productRepository.deleteById(existingId);

        Assertions.assertFalse(productRepository.findById(existingId).isPresent());
    }

    @Test
    public void deleteShouldThrowEmptyResultNotFoundExceptionWhenIdDoesntExists() {


        Assertions.assertThrows(EmptyResultDataAccessException.class, () ->
                productRepository.deleteById(nonExistingId));
    }

    @Test
    public void saveShouldPersistWithAutoIncrementWhenIdisNull() {

        var product = productRepository.save(createProduct());

        Assertions.assertNotNull(product.getId());
        Assertions.assertEquals(quantityOfProducts+1, product.getId());
    }

    @Test
    public void findByIdShouldReturnNonNullOptionalWhenIdExists() {

        var optional = productRepository.findById(existingId);

        Assertions.assertNotNull(optional);
    }

    @Test
    public void findByIdShouldReturnNullOptionalWhenIdDoesntExists() {

        var optional = productRepository.findById(nonExistingId);

        Assertions.assertEquals(optional, Optional.empty());
    }
}
