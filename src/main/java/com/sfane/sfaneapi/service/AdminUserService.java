package com.sfane.sfaneapi.service;

import com.sfane.sfaneapi.model.AdminUser;
import com.sfane.sfaneapi.repository.AdminUserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminUserService implements UserDetailsService {

    private final AdminUserRepository repo;
    private final PasswordEncoder passwordEncoder;

    public AdminUserService(AdminUserRepository repo, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    public AdminUser createAdmin(String username, String rawPassword) {
        if (repo.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }
        AdminUser u = AdminUser.builder()
                .username(username)
                .password(passwordEncoder.encode(rawPassword))
                .role("ADMIN")
                .build();
        return repo.save(u);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AdminUser user = repo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new User(user.getUsername(), user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole())));
    }
    public AdminUser findByUsername(String username) {
        return repo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

}
