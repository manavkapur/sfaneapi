package com.sfane.sfaneapi.controller;

import com.sfane.sfaneapi.model.Order;
import com.sfane.sfaneapi.repository.OrderRepository;
import com.sfane.sfaneapi.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@AllArgsConstructor
@PreAuthorize("hasRole('USER')")
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepo;

    @PostMapping("/create")
    public Order create(Authentication auth){
        Long userId = (Long) auth.getPrincipal();
        return orderService.createOrder(userId);
    }

    @GetMapping
    public List<Order> myOrders(Authentication auth){
        Long userId = (Long) auth.getPrincipal();
        return orderRepo.findByUserId(userId);
    }
}
