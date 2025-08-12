package com.bookstore.security;

import com.bookstore.service.UserService;
import com.bookstore.entity.User;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
                                    throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        String email = jwtService.extractEmail(token);

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtService.isTokenValid(token, email)) {
                User user = userService.getByEmail(email);

                UserDetails userDetails = org.springframework.security.core.userdetails.User
                	    .withUsername(user.getEmail())
                	    .password(user.getPassword()) // Not used for validation here
                	    .authorities(getAuthorities(user.getRole().name()))
                	    .build();
                
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                	    userDetails, null, userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    private java.util.Collection<? extends GrantedAuthority> getAuthorities(String role) {
        return java.util.List.of(() -> "ROLE_" + role);
    }
}
