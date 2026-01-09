package com.sfane.sfaneapi.dto;

import lombok.Data;

@Data
public class AddToCartRequest {
    private Long productId;
    private int qty;
}

