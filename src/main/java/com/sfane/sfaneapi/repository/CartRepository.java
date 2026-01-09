package com.sfane.sfaneapi.repository;

import com.sfane.sfaneapi.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser_Id(Long userId);
}

