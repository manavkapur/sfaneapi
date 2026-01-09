package com.sfane.sfaneapi.controller;

import com.sfane.sfaneapi.dto.CartResponse;
import com.sfane.sfaneapi.service.CartPricingService;
import org.springframework.security.core.Authentication;
import com.sfane.sfaneapi.dto.AddToCartRequest;
import com.sfane.sfaneapi.dto.UpdateCartRequest;
import com.sfane.sfaneapi.model.Cart;
import com.sfane.sfaneapi.service.CartService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@AllArgsConstructor
@PreAuthorize("hasRole('USER')")
public class CartController {

    private final CartService cartService;
    private final CartPricingService pricingService;

    @GetMapping
    public CartResponse view(Authentication auth){
        Long userId = (Long) auth.getPrincipal();
        Cart cart = cartService.getOrCreateCart(userId);
        return pricingService.price(cart);
    }

    @PostMapping("/add")
    public CartResponse add(Authentication auth,
                            @RequestBody AddToCartRequest req){
        Long userId = (Long) auth.getPrincipal();
        Cart cart = cartService.addItem(userId, req.getProductId(), req.getQty());
        return pricingService.price(cart);
    }

    @PutMapping("/update")
    public CartResponse update(Authentication auth,
                               @RequestBody UpdateCartRequest req){
        Long userId = (Long) auth.getPrincipal();
        Cart cart = cartService.updateQty(userId, req.getItemId(), req.getQty());
        return pricingService.price(cart);
    }


    @DeleteMapping("/clear")
    public CartResponse clear(Authentication auth){
        Long userId = (Long) auth.getPrincipal();
        Cart cart = cartService.clear(userId);
        return pricingService.price(cart);
    }

}
