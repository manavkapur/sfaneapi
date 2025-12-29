package com.sfane.sfaneapi.service;


import com.sfane.sfaneapi.model.OfferType;
import com.sfane.sfaneapi.model.Product;
import com.sfane.sfaneapi.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

@Service
public class ProductService {
    private final ProductRepository repo;

    public ProductService(ProductRepository repo){
        this.repo = repo;
    }

    public BigDecimal getEffectivePrice(Product p){
        if(p.getOfferType() == OfferType.PERCENTAGE &&
           p.getDiscountPercent() != null){

            return p.getPrice()
                    .subtract(p.getPrice()
                            .multiply(BigDecimal.valueOf(p.getDiscountPercent()))
                            .divide(BigDecimal.valueOf(100)));
        }

        if(p.getOfferType() == OfferType.TIME_BASED){
            Instant now = Instant.now();
            if(now.isAfter(p.getDiscountStart()) &&
               now.isBefore(p.getDiscountEnd())){
                return p.getPrice();
            }
        }

        return p.getPrice();
    }

}
