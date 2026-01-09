package com.sfane.sfaneapi.repository;

import com.sfane.sfaneapi.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
