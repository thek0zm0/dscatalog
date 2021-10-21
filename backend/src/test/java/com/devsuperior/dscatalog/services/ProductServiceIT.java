package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.Dto.ProductDto;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class ProductServiceIT {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    private Long existingId;
    private Long nonExistingId;
    private Long totalProducts;

    @BeforeEach
    void setup() throws Exception {
        existingId = 1L;
        nonExistingId = 1000L;
        totalProducts = 25L;
    }

    @Test
    public void deleteShouldDeleteResourceWhenIdExists() {

        productService.delete(existingId);

        Assertions.assertEquals(totalProducts-1, productRepository.count());
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesntExists() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            productService.delete(nonExistingId);
        });
    }

    @Test
    public void findAllPagedShouldReturnPageWhenPageZeroTimeTen() {

        PageRequest pageRequest = PageRequest.of(0, 10);

        Page<ProductDto> page = productService.findAllPaged(pageRequest);

        Assertions.assertFalse(page.isEmpty());
        Assertions.assertEquals(0, page.getNumber());
        Assertions.assertEquals(10, page.getSize());
        Assertions.assertEquals(totalProducts, page.getTotalElements());
    }

    @Test
    public void findAllPagedShouldReturnEmptyPageWhenPageDoesntExists() {

        PageRequest pageRequest = PageRequest.of(50, 10);

        Page<ProductDto> page = productService.findAllPaged(pageRequest);

        Assertions.assertTrue(page.isEmpty());
    }

    @Test
    public void findAllPagedShouldReturnSortedPageWhenPageSortedByName() {

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("name"));

        Page<ProductDto> page = productService.findAllPaged(pageRequest);

        Assertions.assertFalse(page.isEmpty());
        Assertions.assertEquals("Macbook Pro", page.getContent().get(0).getName());
        Assertions.assertEquals("PC Gamer", page.getContent().get(1).getName());
        Assertions.assertEquals("PC Gamer Alfa", page.getContent().get(2).getName());
    }
}
