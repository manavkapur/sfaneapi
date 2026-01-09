package com.sfane.sfaneapi.dto;

import lombok.Data;

@Data
public class UpdateCartRequest {
    private Long itemId;
    private int qty;
}

