package com.sfane.sfaneapi.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "user_refresh_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 512)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private Instant expiresAt;

    private boolean revoked = false;
}
