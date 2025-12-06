package com.sfane.sfaneapi.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class TestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;
}
