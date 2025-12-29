package com.sfane.sfaneapi.controller;


import com.sfane.sfaneapi.model.Product;
import com.sfane.sfaneapi.model.ProductImage;
import com.sfane.sfaneapi.repository.ProductRepository;
import com.sfane.sfaneapi.security.CloudinaryService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/products")
@PreAuthorize("hasRole('ADMIN')")
@AllArgsConstructor
public class AdminProductController {

    private final ProductRepository productRepo;
    private final CloudinaryService cloudinaryService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Product createProduct(
            @RequestPart("product") Product product,
            @RequestPart("images") List<MultipartFile> images
    ) throws IOException {
        Product saved = productRepo.save(product);
        for (MultipartFile img : images){
            Map res = cloudinaryService.upload(img);

            ProductImage pi = ProductImage.builder()
                    .imageUrl(res.get("secure_url").toString())
                    .publicId(res.get("public_id").toString())
                    .product(saved)
                    .build();
            saved.getImages().add(pi);
        }
        return productRepo.save(saved);
    }
}
