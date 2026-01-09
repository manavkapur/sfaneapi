package com.sfane.sfaneapi.controller;


import com.sfane.sfaneapi.dto.ProductResponse;
import com.sfane.sfaneapi.model.Category;
import com.sfane.sfaneapi.model.Product;
import com.sfane.sfaneapi.repository.CategoryRepository;
import com.sfane.sfaneapi.repository.ProductRepository;
import com.sfane.sfaneapi.service.OfferEngine;
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
    private final OfferEngine offerEngine;

    @GetMapping("/categories")
    public List<Category> categories() {
        return categoryRepo.findAll();
    }

    @GetMapping("/categories/{slug}/products")
    public List<ProductResponse> productsByCategory(@PathVariable String slug){
        return productRepo.findByCategories_SlugAndActiveTrue(slug)
                .stream()
                .map(offerEngine::apply)
                .toList();
    }
}
