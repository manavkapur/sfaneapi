package com.sfane.sfaneapi.repository;

import com.sfane.sfaneapi.model.RefreshToken;
import com.sfane.sfaneapi.model.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    List<RefreshToken> findByUser(AdminUser user);
    void deleteByUser(AdminUser user);
}
