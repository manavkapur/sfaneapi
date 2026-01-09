package com.sfane.sfaneapi.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private BigDecimal subTotal;
    private BigDecimal discountTotal;
    private BigDecimal grandTotal;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private String paymentId;     // from Razorpay later
    private String paymentMethod; // UPI / CARD / COD

    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<OrderItem> items = new ArrayList<>();

    private Instant createdAt = Instant.now();
}
