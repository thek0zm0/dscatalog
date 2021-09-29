package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.Dto.ProductDto;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
public class ProductService
{
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    // readOnly -> doesnt "lock" database
    @Transactional(readOnly = true)
    public Page<ProductDto> findAllPaged(PageRequest pageRequest)
    {
        /*
        List<Product> list = productRepository.findAll();
        List<ProductDto> listDto = new ArrayList<>();
        for(Product cat : list)
        {
            listDto.add(new ProductDto(cat));
        }
        */
        return productRepository
                .findAll(pageRequest)
                .map(x -> new ProductDto(x));
    }

    @Transactional(readOnly = true)
    public ProductDto findById(Long id)
    {
        Optional<Product> obj = productRepository.findById(id);
        // Throw my exception
        Product entity = obj.orElseThrow( () -> new ResourceNotFoundException("Entity not found"));
        return new ProductDto(entity, entity.getCategories());
    }

    @Transactional
    public ProductDto insert(ProductDto productDto)
    {
        Product entity = new Product();
        copyDtoToEntity(productDto, entity);
        entity = productRepository.save(entity);
        return new ProductDto(entity);
    }

    @Transactional
    public ProductDto update(Long id, ProductDto productDto)
    {
        try
        {
            Product entity = productRepository.getOne(id);
            copyDtoToEntity(productDto, entity);
            entity = productRepository.save(entity);

            return new ProductDto(entity);
        }
        catch (EntityNotFoundException e)
        {
            throw new ResourceNotFoundException("Id not found" + id);
        }
    }

    public void delete(Long id)
    {
        try
        {
            productRepository.deleteById(id);
        }
        catch (EmptyResultDataAccessException e)
        {
            throw new ResourceNotFoundException("Invalid id: "+id);
        }
        catch (DataIntegrityViolationException e)
        {
            throw new DatabaseException("Integrity Violation");
        }
    }

    private void copyDtoToEntity(ProductDto productDto, Product entity)
    {
        entity.setName(productDto.getName());
        entity.setDescription(productDto.getDescription());
        entity.setDate(productDto.getDate());
        entity.setPrice(productDto.getPrice());
        entity.setImgUrl(productDto.getImgUrl());

        entity.getCategories().clear();

        productDto.getCategories().forEach(categoryDto ->
        {
            entity.getCategories().add(categoryRepository.getOne(categoryDto.getId()));
        });
    }
}
