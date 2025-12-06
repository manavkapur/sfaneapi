package com.sfane.sfaneapi.controller;

import com.sfane.sfaneapi.model.TestEntity;
import com.sfane.sfaneapi.repository.TestRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
@CrossOrigin
public class TestController {

    private final TestRepository repository;

    public TestController(TestRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/save")
    public TestEntity saveTest() {
        TestEntity t = new TestEntity();
        t.setMessage("Connection OK");
        return repository.save(t);
    }
}