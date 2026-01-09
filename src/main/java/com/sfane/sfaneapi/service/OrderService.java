package com.sfane.sfaneapi.service;

import com.sfane.sfaneapi.dto.CartItemResponse;
import com.sfane.sfaneapi.dto.CartResponse;
import com.sfane.sfaneapi.model.*;
import com.sfane.sfaneapi.repository.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class OrderService {

    private final CartService cartService;
    private final CartPricingService pricingService;
    private final OrderRepository orderRepo;

    public Order createOrder(Long userId) {

        Cart cart = cartService.getOrCreateCart(userId);

        if(cart.getItems().isEmpty()){
            throw new RuntimeException("Cart is empty");
        }

        CartResponse priced = pricingService.price(cart);

        Order order = Order.builder()
                .userId(userId)
                .subTotal(priced.getSubTotal())
                .discountTotal(priced.getDiscountTotal())
                .grandTotal(priced.getGrandTotal())
                .status(OrderStatus.CREATED)
                .build();

        for(CartItemResponse ci : priced.getItems()){
            order.getItems().add(
                    OrderItem.builder()
                            .order(order)
                            .productId(ci.getProductId())
                            .productName(ci.getName())
                            .image(ci.getImage())
                            .price(ci.getPrice())
                            .finalPrice(ci.getFinalPrice())
                            .qty(ci.getQty())
                            .freeQty(ci.getFreeQty())
                            .itemTotal(ci.getItemTotal())
                            .build()
            );
        }

        Order saved = orderRepo.save(order);

        cartService.clear(userId); // 🔥 CRITICAL

        return saved;
    }
}
