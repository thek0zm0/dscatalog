package com.devsuperior.dscatalog.factory;

import com.devsuperior.dscatalog.Dto.ProductDto;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;

import java.time.Instant;

public class Factory {

    public static Product createProduct() {

        Product product = new Product(null,"Produto Teste Nome","Produto Teste Descricao",
                999.99,"/caminho/teste", Instant.now());
        product.getCategories().add(createCategory());
        return product;
    }

    public static ProductDto createProductDto() {

        return new ProductDto(createProduct(), createProduct().getCategories());
    }

    public static Category createCategory() {

        Category category = new Category(1L,"Books");

        return category;
    }
}
