package com.sfane.sfaneapi.controller;

import com.sfane.sfaneapi.dto.ProductResponse;
import com.sfane.sfaneapi.model.Product;
import com.sfane.sfaneapi.repository.ProductRepository;
import com.sfane.sfaneapi.service.OfferEngine;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products")
@AllArgsConstructor
public class ProductController {
    private final ProductRepository repo;
    private final OfferEngine offerEngine;

    @GetMapping
    public List<ProductResponse> list(){
        return repo.findByActiveTrue()
                .stream()
                .map(offerEngine::apply)
                .toList();

    }
}
