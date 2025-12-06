package com.sfane.sfaneapi.config;

import com.sfane.sfaneapi.repository.AdminUserRepository;
import com.sfane.sfaneapi.service.AdminUserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

@Configuration
public class DataSeeder {

    @Value("${app.admin.defaultUsername:admin}")
    private String defaultUsername;

    @Value("${app.admin.defaultPassword:admin123}")
    private String defaultPassword;

    @Bean
    CommandLineRunner init(AdminUserRepository repo, AdminUserService service) {
        return args -> {
            if (!repo.existsByUsername(defaultUsername)) {
                service.createAdmin(defaultUsername, defaultPassword);
                System.out.println("Created default admin user -> " + defaultUsername);
            } else {
                System.out.println("Default admin already exists");
            }
        };
    }
}
