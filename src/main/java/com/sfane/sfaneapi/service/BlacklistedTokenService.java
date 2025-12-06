package com.sfane.sfaneapi.service;

import com.sfane.sfaneapi.model.BlacklistedToken;
import com.sfane.sfaneapi.repository.BlacklistedTokenRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class BlacklistedTokenService {

    private final BlacklistedTokenRepository repo;

    public BlacklistedTokenService(BlacklistedTokenRepository repo) {
        this.repo = repo;
    }

    public void blacklist(String token, Instant expiresAt, String reason) {
        BlacklistedToken bt = BlacklistedToken.builder()
                .token(token)
                .createdAt(Instant.now())
                .expiresAt(expiresAt)
                .reason(reason)
                .build();
        repo.save(bt);
    }

    public boolean isBlacklisted(String token) {
        return repo.existsByToken(token);
    }
}
