package com.sfane.sfaneapi.controller;


import com.sfane.sfaneapi.model.Product;
import com.sfane.sfaneapi.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/products")
@PreAuthorize("hasRole('ADMIN')")
@AllArgsConstructor
public class AdminOfferController {
    private final ProductRepository productRepo;

    @PutMapping("/{id}/offer")
    public Product updateOffer(
            @PathVariable Long id,
            @RequestBody Product offerUpdate
    ){
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setOfferType(offerUpdate.getOfferType());
        product.setDiscountPercent(offerUpdate.getDiscountPercent());
        product.setBuyQty(offerUpdate.getBuyQty());
        product.setGetQty(offerUpdate.getGetQty());
        product.setDiscountStart(offerUpdate.getDiscountStart());
        product.setDiscountEnd(offerUpdate.getDiscountEnd());
        product.setUpdatedAt(java.time.Instant.now());

        return productRepo.save(product);
    }
}
