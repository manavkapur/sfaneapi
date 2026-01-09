package com.sfane.sfaneapi.service;

import com.sfane.sfaneapi.model.Cart;
import com.sfane.sfaneapi.model.CartItem;
import com.sfane.sfaneapi.model.Product;
import com.sfane.sfaneapi.model.User;
import com.sfane.sfaneapi.repository.CartRepository;
import com.sfane.sfaneapi.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CartService {

    private final CartRepository cartRepo;
    private final ProductRepository productRepo;

    public Cart getOrCreateCart(Long userId){
        return cartRepo.findByUser_Id(userId)
                .orElseGet(() -> cartRepo.save(
                        Cart.builder()
                                .user(User.builder().id(userId).build())
                                .build()
                ));
    }

    public Cart addItem(Long userId, Long productId, int qty){

        Cart cart = getOrCreateCart(userId);

        for(CartItem item : cart.getItems()){
            if(item.getProduct().getId().equals(productId)){
                item.setQuantity(item.getQuantity() + qty);
                return cartRepo.save(cart);
            }
        }

        Product product = productRepo.findById(productId)
                .orElseThrow();

        cart.getItems().add(
                CartItem.builder()
                        .cart(cart)
                        .product(product)
                        .quantity(qty)
                        .build()
        );

        return cartRepo.save(cart);
    }

    public Cart updateQty(Long userId, Long itemId, int qty){

        Cart cart = getOrCreateCart(userId);

        cart.getItems().removeIf(i -> {
            if(i.getId().equals(itemId)){
                if(qty <= 0) return true;
                i.setQuantity(qty);
            }
            return false;
        });

        return cartRepo.save(cart);
    }

    public Cart clear(Long userId){
        Cart cart = getOrCreateCart(userId);
        cart.getItems().clear();
        return cartRepo.save(cart);
    }
}

