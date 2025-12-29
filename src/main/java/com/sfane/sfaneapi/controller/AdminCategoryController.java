package com.sfane.sfaneapi.controller;


import com.sfane.sfaneapi.model.Category;
import com.sfane.sfaneapi.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/categories")
@PreAuthorize("hasRole('ADMIN')")
@AllArgsConstructor
public class AdminCategoryController {

    private final CategoryRepository repo;

    @PostMapping
    public Category create(@RequestBody Category category){
        return repo.save(category);
    }
}
