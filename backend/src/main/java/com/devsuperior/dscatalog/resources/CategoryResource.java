package com.devsuperior.dscatalog.resources;

import com.devsuperior.dscatalog.Dto.CategoryDto;
import com.devsuperior.dscatalog.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


import java.net.URI;

@RestController
@RequestMapping(value = "/categories")
public class CategoryResource
{
    @Autowired
    private CategoryService categoryService;

    /* Encapsulate Http request */
    @GetMapping
    public ResponseEntity<Page<CategoryDto>> findAll(Pageable pageable)
    {
        Page<CategoryDto> list = categoryService.findAllPaged(pageable);
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<CategoryDto> findById(@PathVariable Long id)
    {
        CategoryDto cat = categoryService.findById(id);
        return ResponseEntity.ok().body(cat);
    }

    @PostMapping
    public ResponseEntity<CategoryDto> insert(@RequestBody CategoryDto categoryDto)
    {
        categoryDto = categoryService.insert(categoryDto);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(categoryDto.getId())
                .toUri();
        return ResponseEntity.created(uri).body(categoryDto);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<CategoryDto> update(@PathVariable Long id,
                                              @RequestBody CategoryDto categoryDto)
    {
        categoryDto = categoryService.update(id, categoryDto);
        return ResponseEntity.ok().body(categoryDto);
    }
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<CategoryDto> delete(@PathVariable Long id)
    {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
