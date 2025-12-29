package com.sfane.sfaneapi.repository;

import com.sfane.sfaneapi.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByActiveTrue();

    List<Product> findByCategories_SlugAndActiveTrue(String slug);
}


