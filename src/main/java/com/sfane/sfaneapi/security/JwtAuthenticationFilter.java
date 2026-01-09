package com.sfane.sfaneapi.security;

import com.sfane.sfaneapi.service.AdminUserService;
import com.sfane.sfaneapi.service.BlacklistedTokenService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final AdminUserService userService;
    private final BlacklistedTokenService blacklistedTokenService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, AdminUserService userService, BlacklistedTokenService blacklistedTokenService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.blacklistedTokenService = blacklistedTokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);
            if (StringUtils.hasText(jwt)) {
                // Check blacklist first
                if (blacklistedTokenService.isBlacklisted(jwt)) {
                    // token is blacklisted — reject
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Token revoked");
                    return;
                }

                if (jwtUtil.validateToken(jwt)) {
                    String username = jwtUtil.getUsernameFromToken(jwt);

                    if(username.startsWith("USER_")){
                        Long userId = Long.parseLong(username.replace("USER_", ""));

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(userId, null, List.of(new SimpleGrantedAuthority("ROLE_USER")) );

                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } else {
                        UserDetails userDetails = userService.loadUserByUsername(username);



                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }



                }
            }
        } catch (Exception ex) {
            // optional: log
        }
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}
