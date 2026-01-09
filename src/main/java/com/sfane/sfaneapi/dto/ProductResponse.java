package com.sfane.sfaneapi.dto;


import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String slug;
    private String description;

    private BigDecimal price;
    private BigDecimal finalPrice;

    private Integer buyQty;
    private Integer getQty;
    private Integer freeQty;

    private String offerType;
    private Integer discountPercent;

    private boolean offerActive;

    private List<String> imageUrls;

}
