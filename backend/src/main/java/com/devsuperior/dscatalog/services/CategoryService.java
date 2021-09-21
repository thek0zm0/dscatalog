package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.Dto.CategoryDto;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService
{
    @Autowired
    private CategoryRepository categoryRepository;

    // readOnly -> doesnt "lock" database
    @Transactional(readOnly = true)
    public List<CategoryDto> findAll()
    {
        /*
        List<Category> list = categoryRepository.findAll();
        List<CategoryDto> listDto = new ArrayList<>();
        for(Category cat : list)
        {
            listDto.add(new CategoryDto(cat));
        }
        */
        return categoryRepository
                .findAll()
                .stream()
                .map(x -> new CategoryDto(x))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryDto findById(Long id)
    {
        Optional<Category> obj = categoryRepository.findById(id);
        // Throw my exception
        Category entity = obj.orElseThrow( () -> new ResourceNotFoundException("Entity not found"));
        return new CategoryDto(entity);
    }

    @Transactional
    public CategoryDto insert(CategoryDto categoryDto)
    {
        Category entity = new Category();
        entity.setName(categoryDto.getName());
        entity = categoryRepository.save(entity);
        return new CategoryDto(entity);
    }

    @Transactional
    public CategoryDto update(Long id, CategoryDto categoryDto)
    {
        try
        {
            Category entity = categoryRepository.getOne(id);
            entity.setName(categoryDto.getName());
            entity = categoryRepository.save(entity);

            return new CategoryDto(entity);
        }
        catch (EntityNotFoundException e)
        {
            throw new ResourceNotFoundException("Id not found" + id);
        }
    }
}
