package com.sfane.sfaneapi.repository;

import com.sfane.sfaneapi.model.TestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestRepository extends JpaRepository<TestEntity, Long> {}