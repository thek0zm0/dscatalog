package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.Dto.ProductDto;
import com.devsuperior.dscatalog.factory.Factory;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    private Long existingId;
    private Long nonExistingId;
    private Long dependingId;
    private PageImpl<Product> page;
    private Product product;
    private Category category;
    private ProductDto productDto;

    @BeforeEach
    void setup() throws Exception {
        existingId = 1L;
        nonExistingId = 2L;
        dependingId = 3L;
        product = Factory.createProduct();
        category = Factory.createCategory();
        productDto = Factory.createProductDto();
        page = new PageImpl<>(List.of(product));

        Mockito.when(categoryRepository.getOne(ArgumentMatchers.any())).thenReturn(category);

        Mockito.when(productRepository.findAll((Pageable)ArgumentMatchers.any())).thenReturn(page);

        Mockito.when(productRepository.save(ArgumentMatchers.any())).thenReturn(product);

        Mockito.when(productRepository.getOne(existingId)).thenReturn(product);
        Mockito.doThrow(EntityNotFoundException.class).when(productRepository).getOne(nonExistingId);

        Mockito.when(productRepository.findById(existingId)).thenReturn(Optional.ofNullable(product));
        Mockito.when(productRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        Mockito.doNothing().when(productRepository).deleteById(existingId);
        Mockito.doThrow(EmptyResultDataAccessException.class).when(productRepository).deleteById(nonExistingId);
        Mockito.doThrow(DataIntegrityViolationException.class).when(productRepository).deleteById(dependingId);
    }

    @Test
    public void deleteShouldDoNothingWhenIdExists() {

        Assertions.assertDoesNotThrow(() -> {
            service.delete(existingId);
        });

        Mockito.verify(productRepository, Mockito.times(1)).deleteById(existingId);
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesntExists() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(nonExistingId);
        });

        Mockito.verify(productRepository, Mockito.times(1)).deleteById(nonExistingId);
    }

    @Test
    public void deleteShouldThrowDatabaseExceptionWhenIdDependent() {

        Assertions.assertThrows(DatabaseException.class, () -> {
            service.delete(dependingId);
        });

        Mockito.verify(productRepository, Mockito.times(1)).deleteById(dependingId);
    }

    @Test
    public void findAllPagedShouldReturnPage() {

        Pageable page = PageRequest.of(0, 10);

        Page<ProductDto> result = service.findAllPaged(page);

        Assertions.assertNotNull(result);

        Mockito.verify(productRepository, Mockito.times(1)).findAll(page);
    }

    @Test
    public void findByIdShouldReturnProductDtoWhenIdExists() {

        Assertions.assertDoesNotThrow(() -> {
            service.findById(existingId);
        });

        Mockito.verify(productRepository, Mockito.times(1)).findById(existingId);
    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesntExists() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(nonExistingId);
        });

        Mockito.verify(productRepository, Mockito.times(1)).findById(nonExistingId);
    }

    @Test
    public void updateShouldReturnProductDtoWhenIdExists() {

        Assertions.assertDoesNotThrow(() -> {
            service.update(existingId, productDto);
        });

        Mockito.verify(productRepository, Mockito.times(1)).save(product);
    }

    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesntExists() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.update(nonExistingId, productDto);
        });

        Mockito.verify(productRepository, Mockito.times(1)).getOne(nonExistingId);
    }
}
