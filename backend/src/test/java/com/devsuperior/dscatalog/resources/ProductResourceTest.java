package com.devsuperior.dscatalog.resources;

import com.devsuperior.dscatalog.Dto.ProductDto;
import com.devsuperior.dscatalog.factory.Factory;
import com.devsuperior.dscatalog.services.ProductService;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

@WebMvcTest(ProductResource.class)
public class ProductResourceTest {

    private final String API = "/products/";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private Long existingId;
    private Long nonExistingId;
    private Long dependentId;
    private ProductDto productDto;
    private PageImpl<ProductDto> page;

    @BeforeEach
    void setup() throws Exception {
        existingId = 1L;
        nonExistingId = 2L;
        dependentId = 3L;
        productDto = Factory.createProductDto();
        page = new PageImpl<>(List.of(productDto));

        Mockito.when(productService.findAllPaged(ArgumentMatchers.any())).thenReturn(page);

        Mockito.when(productService.findById(existingId)).thenReturn(productDto);
        Mockito.when(productService.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

        Mockito.when(productService.update(eq(existingId), ArgumentMatchers.any())).thenReturn(productDto);
        Mockito.when(productService.update(eq(nonExistingId), ArgumentMatchers.any())).thenThrow(ResourceNotFoundException.class);

        Mockito.when(productService.insert(ArgumentMatchers.any())).thenReturn(productDto);

        Mockito.doNothing().when(productService).delete(existingId);
        Mockito.doThrow(ResourceNotFoundException.class).when(productService).delete(nonExistingId);
        Mockito.doThrow(DatabaseException.class).when(productService).delete(dependentId);
    }

    @Test
    public void findAllShouldReturnPage() throws Exception {
        mvc.perform(get(API)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void findByIdShouldReturnProductDtoWhenIdExists() throws Exception {
        mvc.perform(get(API + existingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.description").exists());
    }

    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesntExists() throws Exception {
        mvc.perform(get(API + nonExistingId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateShouldReturnProductDtoWhenIdExists() throws Exception {
        var jsonBody = objectMapper.writeValueAsString(productDto);

        mvc.perform(put(API + existingId)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.description").exists());
    }

    @Test
    public void updateShouldReturnNotFoundWhenIdDoesntExists() throws Exception {
        var jsonBody = objectMapper.writeValueAsString(productDto);

        mvc.perform(put(API + nonExistingId)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void insertShouldReturnProductDto() throws Exception {
        var jsonBody = objectMapper.writeValueAsString(productDto);

        mvc.perform(post(API)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.description").exists());
    }

    @Test
    public void deleteShouldDoNothingWhenIdExists() throws Exception {
        mvc.perform(delete(API + existingId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteShouldReturnNotFoundExceptionWhenIdDoesntExists() throws Exception {
        mvc.perform(delete(API + nonExistingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
