package com.sfane.sfaneapi.repository;

import com.sfane.sfaneapi.model.RefreshToken;
import com.sfane.sfaneapi.model.UserRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRefreshTokenRepository extends JpaRepository<UserRefreshToken, Long> {
    Optional<UserRefreshToken> findByToken(String token);
}
