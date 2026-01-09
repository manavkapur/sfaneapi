package com.sfane.sfaneapi.service;


import com.sfane.sfaneapi.model.RefreshToken;
import com.sfane.sfaneapi.model.User;
import com.sfane.sfaneapi.model.UserRefreshToken;
import com.sfane.sfaneapi.repository.UserRefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class UserRefreshTokenService {

    private final UserRefreshTokenRepository repo;
    private final long refreshExpirationMs;

    public UserRefreshTokenService(
            UserRefreshTokenRepository repo,
            @Value("${app.jwt.refreshExpirationMs}") long refreshExpirationMs
    ) {
        this.repo = repo;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    public UserRefreshToken create(User user) {
        UserRefreshToken rt = UserRefreshToken.builder()
                .token(UUID.randomUUID() + "-" + UUID.randomUUID())
                .user(user)
                .expiresAt(Instant.now().plusMillis(refreshExpirationMs))
                .revoked(false)
                .build();
        return repo.save(rt);
    }

    public boolean isExpired(UserRefreshToken token) {
        return token.getExpiresAt().isBefore(Instant.now());
    }

    public UserRefreshToken findByToken(String token) {
        return repo.findByToken(token).orElse(null);
    }

    public void revoke(UserRefreshToken token) {
        token.setRevoked(true);
        repo.save(token);
    }


}
