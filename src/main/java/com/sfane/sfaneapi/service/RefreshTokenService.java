package com.sfane.sfaneapi.service;

import com.sfane.sfaneapi.model.AdminUser;
import com.sfane.sfaneapi.model.RefreshToken;
import com.sfane.sfaneapi.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repo;
    private final long refreshExpirationMs;

    public RefreshTokenService(RefreshTokenRepository repo,
                               @Value("${app.jwt.refreshExpirationMs}") long refreshExpirationMs) {
        this.repo = repo;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    public RefreshToken createRefreshToken(AdminUser user) {
        RefreshToken rt = RefreshToken.builder()
                .token(generateToken())
                .user(user)
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plusMillis(refreshExpirationMs))
                .revoked(false)
                .build();
        return repo.save(rt);
    }

    private String generateToken() {
        // use UUID + random prefix to ensure uniqueness/entropy
        return UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString();
    }

    public boolean isExpired(RefreshToken token) {
        return token.getExpiresAt().isBefore(Instant.now());
    }

    public void revoke(RefreshToken token) {
        token.setRevoked(true);
        repo.save(token);
    }

    public void revokeAllForUser(AdminUser user) {
        repo.findByUser(user).forEach(t -> {
            t.setRevoked(true);
        });
        repo.saveAll(repo.findByUser(user));
    }

    public RefreshToken findByToken(String token) {
        return repo.findByToken(token).orElse(null);
    }
}
