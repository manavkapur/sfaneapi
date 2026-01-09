package com.sfane.sfaneapi.service;

import com.sfane.sfaneapi.dto.*;
import com.sfane.sfaneapi.model.Cart;
import com.sfane.sfaneapi.model.CartItem;
import com.sfane.sfaneapi.model.Product;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class CartPricingService {

    private final OfferEngine offerEngine;

    public CartResponse price(Cart cart) {

        List<CartItemResponse> items = new ArrayList<>();
        BigDecimal subTotal = BigDecimal.ZERO;
        BigDecimal grandTotal = BigDecimal.ZERO;

        for (CartItem ci : cart.getItems()) {

            Product p = ci.getProduct();
            var offer = offerEngine.apply(p);
            int qty = ci.getQuantity();

            BigDecimal itemMrpTotal =
                    p.getPrice().multiply(BigDecimal.valueOf(qty));

            BigDecimal itemFinalTotal =
                    offer.getFinalPrice().multiply(BigDecimal.valueOf(qty));

            items.add(CartItemResponse.builder()
                    .productId(p.getId())
                    .name(p.getName())
                    .image(
                            p.getImages().isEmpty()
                                    ? null
                                    : p.getImages().get(0).getImageUrl()
                    )
                    .price(p.getPrice())
                    .finalPrice(offer.getFinalPrice())
                    .qty(qty)
                    .freeQty(offer.getFreeQty())
                    .offerApplied(offer.isOfferActive())
                    .itemTotal(itemFinalTotal)
                    .build()
            );

            subTotal = subTotal.add(itemMrpTotal);
            grandTotal = grandTotal.add(itemFinalTotal);
        }

        return CartResponse.builder()
                .cartId(cart.getId())
                .items(items)
                .subTotal(subTotal)
                .grandTotal(grandTotal)
                .discountTotal(subTotal.subtract(grandTotal))
                .build();
    }
}
