package com.sfane.sfaneapi.controller;


import com.sfane.sfaneapi.model.Category;
import com.sfane.sfaneapi.model.Product;
import com.sfane.sfaneapi.repository.CategoryRepository;
import com.sfane.sfaneapi.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepo;
    private final ProductRepository productRepo;

    @GetMapping("/categories")
    public List<Category> categories() {
        return categoryRepo.findAll();
    }

    @GetMapping("/categories/{slug}/products")
    public List<Product> productsByCategory(@PathVariable String slug){
        return productRepo.findByCategories_SlugAndActiveTrue(slug);
    }
}
