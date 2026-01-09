package com.sfane.sfaneapi.repository;

import com.sfane.sfaneapi.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {}

