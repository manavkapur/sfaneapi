package com.sfane.sfaneapi.model;
import com.sfane.sfaneapi.dto.CartItemResponse;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CartResponse {

    private Long cartId;
    private List<CartItemResponse> items;

    private BigDecimal subTotal;
    private BigDecimal discountTotal;
    private BigDecimal grandTotal;
}

