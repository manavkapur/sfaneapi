package com.sfane.sfaneapi.service;


import com.sfane.sfaneapi.dto.ProductResponse;
import com.sfane.sfaneapi.model.Product;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.stream.Collectors;

@Service
public class OfferEngine {

    public ProductResponse apply(Product p){
        BigDecimal finalPrice = p.getPrice();
        int freeQty = 0;
        boolean offerActive = false;

        Instant now = Instant.now();

        switch (p.getOfferType()){
            case PERCENTAGE -> {
                if (p.getDiscountPercent() != null){
                   finalPrice = p.getPrice()
                           .subtract(
                                   p.getPrice()
                                           .multiply(BigDecimal.valueOf(p.getDiscountPercent()))
                                           .divide(BigDecimal.valueOf(100))
                           );
                   offerActive = true;
                }
            }

            case BUY_X_GET_Y -> {
                if(p.getBuyQty() != null && p.getGetQty() != null){
                    freeQty = p.getGetQty();
                    offerActive = true;
                }
            }

            case TIME_BASED -> {
                if (p.getDiscountStart() != null &&
                        p.getDiscountEnd() != null &&
                        now.isAfter(p.getDiscountStart()) &&
                        now.isBefore(p.getDiscountEnd())) {

                    finalPrice = p.getPrice(); // discounted price
                    offerActive = true;
                } else {
                    finalPrice = p.getOriginalPrice() != null
                            ? p.getOriginalPrice()
                            : p.getPrice();
                }
            }
        }

        return ProductResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .slug(p.getSlug())
                .description(p.getDescription())
                .price(p.getPrice())
                .finalPrice(finalPrice)
                .buyQty(p.getBuyQty())
                .getQty(p.getGetQty())
                .freeQty(freeQty)
                .offerType(p.getOfferType().name())
                .discountPercent(p.getDiscountPercent())
                .offerActive(offerActive)
                .imageUrls(
                        p.getImages().stream()
                                .map(img-> img.getImageUrl())
                                .collect(Collectors.toList())
                )
                .build();
    }
}
