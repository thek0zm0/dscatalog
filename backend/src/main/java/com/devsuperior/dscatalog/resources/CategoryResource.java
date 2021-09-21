package com.devsuperior.dscatalog.resources;

import com.devsuperior.dscatalog.Dto.CategoryDto;
import com.devsuperior.dscatalog.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping(value = "/categories")
public class CategoryResource
{
    @Autowired
    private CategoryService categoryService;

    /* Encapsulate Http request */
    @GetMapping
    public ResponseEntity<List<CategoryDto>> findAll()
    {
        List<CategoryDto> list = categoryService.findAll();
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<CategoryDto> findById(@PathVariable Long id)
    {
        CategoryDto cat = categoryService.findById(id);
        return ResponseEntity.ok().body(cat);
    }
}
