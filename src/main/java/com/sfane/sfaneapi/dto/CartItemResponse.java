package com.sfane.sfaneapi.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CartItemResponse {
    private Long productId;
    private String name;
    private String image;
    private BigDecimal price;
    private BigDecimal finalPrice;
    private int qty;
    private int freeQty;
    private boolean offerApplied;
    private BigDecimal itemTotal;
}

