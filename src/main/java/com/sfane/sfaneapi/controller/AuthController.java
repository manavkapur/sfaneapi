package com.sfane.sfaneapi.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.sfane.sfaneapi.config.FirebaseConfig;
import com.sfane.sfaneapi.dto.*;
import com.sfane.sfaneapi.model.AdminUser;
import com.sfane.sfaneapi.model.RefreshToken;
import com.sfane.sfaneapi.model.User;
import com.sfane.sfaneapi.model.UserRefreshToken;
import com.sfane.sfaneapi.security.JwtUtil;
import com.sfane.sfaneapi.service.UserService;
import com.sfane.sfaneapi.service.AdminUserService;
import com.sfane.sfaneapi.service.RefreshTokenService;
import com.sfane.sfaneapi.service.BlacklistedTokenService;
import com.sfane.sfaneapi.service.UserRefreshTokenService;
import org.springframework.http.HttpStatus;
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
    private final AdminUserService adminUserService;
    private final RefreshTokenService refreshTokenService;
    private final BlacklistedTokenService blacklistedTokenService;
    private final UserService userService;
    private final UserRefreshTokenService userRefreshTokenService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          AdminUserService adminUserService,
                          RefreshTokenService refreshTokenService,
                          BlacklistedTokenService blacklistedTokenService,
                          UserService userService,
                          UserRefreshTokenService userRefreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.adminUserService = adminUserService;
        this.refreshTokenService = refreshTokenService;
        this.blacklistedTokenService = blacklistedTokenService;
        this.userService = userService;
        this.userRefreshTokenService = userRefreshTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = jwtUtil.generateToken(authentication.getName());

        AdminUser user = adminUserService.findByUsername(authentication.getName());
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

    @PostMapping("/phone-login")
    public ResponseEntity<?> phoneLogin(@RequestBody Map<String, String> body)
        throws FirebaseAuthException {
        String firebaseToken = body.get("firebaseToken");

        if (firebaseToken == null) {
            return ResponseEntity.badRequest().body("firebaseToken is required");
        }

        // Verify firebase token
        FirebaseToken decodeToken = FirebaseAuth.getInstance().verifyIdToken(firebaseToken);

        String phone = (String) decodeToken.getClaims().get("phone_number");

        if(phone == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Phone number not found in Firebase token");
        }

        User user = userService.findOrCreateByPhone(phone);

        String accessToken = jwtUtil.generateToken("USER_" + user.getId());
        UserRefreshToken refreshToken = userRefreshTokenService.create(user);

        return ResponseEntity.ok(Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken.getToken(),
                "userId", user.getId()
        ));

    }

    @PostMapping("/user/refresh")
    public ResponseEntity<?> userRefresh(@RequestBody Map<String, String> body) {

        String refreshTokenValue = body.get("refreshToken");
        if (refreshTokenValue == null) {
            return ResponseEntity.badRequest().body("refreshToken is required");
        }

        UserRefreshToken rt = userRefreshTokenService.findByToken(refreshTokenValue);

        if (rt == null) {
            return ResponseEntity.status(401).body("Invalid refresh token");
        }

        if (rt.isRevoked()) {
            return ResponseEntity.status(401).body("Refresh token revoked");
        }

        if (userRefreshTokenService.isExpired(rt)) {
            return ResponseEntity.status(401).body("Refresh token expired");
        }

        // Create new USER access token
        String newAccessToken = jwtUtil.generateToken("USER_" + rt.getUser().getId());

        return ResponseEntity.ok(Map.of(
                "accessToken", newAccessToken,
                "tokenType", "Bearer"
        ));
    }

    @PostMapping("/user/logout")
    public ResponseEntity<?> userLogout(@RequestBody Map<String, String> body) {

        String refreshTokenValue = body.get("refreshToken");
        String accessTokenValue = body.get("accessToken");

        // Revoke refresh token
        if (refreshTokenValue != null) {
            UserRefreshToken rt = userRefreshTokenService.findByToken(refreshTokenValue);
            if (rt != null) {
                userRefreshTokenService.revoke(rt);
            }
        }

        // Blacklist access token
        if (accessTokenValue != null) {
            Instant expiry = jwtUtil.getExpirationFromToken(accessTokenValue);
            blacklistedTokenService.blacklist(
                    accessTokenValue,
                    expiry,
                    "user logout"
            );
        }

        return ResponseEntity.ok("User logged out successfully");
    }

}
