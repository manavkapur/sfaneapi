package com.sfane.sfaneapi.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CartItemResponse {

    private Long productId;
    private String name;
    private String image;
    private BigDecimal price;       // MRP
    private BigDecimal finalPrice;  // after offer
    private int qty;
    private int freeQty;
    private boolean offerApplied;

    private BigDecimal itemTotal;   // finalPrice * qty
}
