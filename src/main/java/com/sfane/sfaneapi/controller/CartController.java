package com.sfane.sfaneapi.controller;

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

    @GetMapping
    public Cart view(Authentication auth){
        Long userId = (Long) auth.getPrincipal();
        return cartService.getOrCreateCart(userId);
    }

    @PostMapping("/add")
    public Cart add(
            Authentication auth,
            @RequestBody AddToCartRequest req){

        Long userId = (Long) auth.getPrincipal();
        return cartService.addItem(userId, req.getProductId(), req.getQty());
    }

    @PutMapping("/update")
    public Cart update(
            Authentication auth,
            @RequestBody UpdateCartRequest req){

        Long userId = (Long) auth.getPrincipal();
        return cartService.updateQty(userId, req.getItemId(), req.getQty());
    }

    @DeleteMapping("/clear")
    public Cart clear(Authentication auth){
        Long userId = (Long) auth.getPrincipal();
        return cartService.clear(userId);
    }
}
