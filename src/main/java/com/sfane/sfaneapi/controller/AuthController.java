package com.sfane.sfaneapi.controller;

import com.sfane.sfaneapi.dto.*;
import com.sfane.sfaneapi.model.AdminUser;
import com.sfane.sfaneapi.model.RefreshToken;
import com.sfane.sfaneapi.security.JwtUtil;
import com.sfane.sfaneapi.service.AdminUserService;
import com.sfane.sfaneapi.service.RefreshTokenService;
import com.sfane.sfaneapi.service.BlacklistedTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final AdminUserService userService;
    private final RefreshTokenService refreshTokenService;
    private final BlacklistedTokenService blacklistedTokenService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          AdminUserService userService,
                          RefreshTokenService refreshTokenService,
                          BlacklistedTokenService blacklistedTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        this.blacklistedTokenService = blacklistedTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = jwtUtil.generateToken(authentication.getName());

        AdminUser user = userService.findByUsername(authentication.getName());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return ResponseEntity.ok(Map.of(
                "accessToken", accessToken,
                "tokenType", "Bearer",
                "refreshToken", refreshToken.getToken(),
                "refreshExpiresAt", refreshToken.getExpiresAt().toString()
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> body) {
        String refreshTokenValue = body.get("refreshToken");
        if (refreshTokenValue == null) {
            return ResponseEntity.badRequest().body("refreshToken is required");
        }

        RefreshToken rt = refreshTokenService.findByToken(refreshTokenValue);
        if (rt == null) {
            return ResponseEntity.status(401).body("Invalid refresh token");
        }
        if (rt.isRevoked()) {
            return ResponseEntity.status(401).body("Refresh token revoked");
        }
        if (refreshTokenService.isExpired(rt)) {
            // Optionally blacklist it
            blacklistedTokenService.blacklist(rt.getToken(), rt.getExpiresAt(), "expired");
            return ResponseEntity.status(401).body("Refresh token expired");
        }

        // valid -> create new access token
        String newAccessToken = jwtUtil.generateToken(rt.getUser().getUsername());
        return ResponseEntity.ok(Map.of(
                "accessToken", newAccessToken,
                "tokenType", "Bearer"
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> body) {
        String refreshTokenValue = body.get("refreshToken");
        String accessTokenValue = body.get("accessToken");

        // Revoke refresh token (mark revoked)
        if (refreshTokenValue != null) {
            RefreshToken rt = refreshTokenService.findByToken(refreshTokenValue);
            if (rt != null) {
                refreshTokenService.revoke(rt);
                // also blacklist refresh token value to prevent reuse
                blacklistedTokenService.blacklist(rt.getToken(), rt.getExpiresAt(), "user logout");
            }
        }

        // Blacklist access token if provided
        if (accessTokenValue != null) {
            // get expiry from JWT util
            Instant expiry = jwtUtil.getExpirationFromToken(accessTokenValue);
            blacklistedTokenService.blacklist(accessTokenValue, expiry, "user logout access token");
        }

        return ResponseEntity.ok("Logged out");
    }
}
