package com.sfane.sfaneapi.service;


import com.sfane.sfaneapi.model.User;
import com.sfane.sfaneapi.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository repo;

    public UserService(UserRepository repo){
        this.repo = repo;
    }

    public User findOrCreateByPhone(String phone){
        return repo.findByPhone(phone)
                .orElseGet(() -> repo.save(
                        User.builder().phone(phone).build()
                ));
    }
}
