package com.bookstore.config;

import com.bookstore.security.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.Customizer;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())   // ✅ enable CORS with your CorsConfig
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                
                .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/refresh-token").permitAll()
                .requestMatchers("/api/auth/admin/**").hasRole("ADMIN")
                    

                .requestMatchers(HttpMethod.POST, "/api/books/add").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/books/bulk-upload").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/books/update/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/books/delete/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/books/delete-all").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/books/get/low-stock").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/books/recommendations").hasAnyRole("CUSTOMER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/books/get/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/books/get-all").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/books/search").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/books/get/isbn/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/books/trending").permitAll()

                
                
                .requestMatchers(HttpMethod.POST, "/api/cart/add").hasRole("CUSTOMER")
                .requestMatchers(HttpMethod.PUT, "/api/cart/update").hasRole("CUSTOMER")
                .requestMatchers(HttpMethod.GET, "/api/cart/get").hasRole("CUSTOMER")
                .requestMatchers(HttpMethod.DELETE, "/api/cart/remove/**").hasRole("CUSTOMER")
                .requestMatchers(HttpMethod.DELETE, "/api/cart/clear").hasRole("CUSTOMER")

                
                
                .requestMatchers(HttpMethod.POST, "/api/categories/add").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/categories/update/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/categories/delete/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/categories/get-all").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/categories/get/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/categories/get/name/**").permitAll()

                
                
                .requestMatchers(HttpMethod.POST, "/api/orders/place").hasRole("CUSTOMER")
                .requestMatchers(HttpMethod.GET, "/api/orders/get/my-orders").hasRole("CUSTOMER")
                .requestMatchers(HttpMethod.DELETE, "/api/orders/cancel/**").hasRole("CUSTOMER")
                .requestMatchers(HttpMethod.GET, "/api/orders/get-all").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/orders/get/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/orders/*/status").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/orders/*/invoice").authenticated()

                
                
                .requestMatchers(HttpMethod.POST, "/api/payments/pay").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/payments/confirm").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/payments/get-all").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/payments/{paymentId}").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/payments/order/**").authenticated()

                
                
                .requestMatchers(HttpMethod.POST, "/api/reviews/add/**").hasRole("CUSTOMER")
                .requestMatchers(HttpMethod.GET, "/api/reviews/get/user/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/reviews/delete/**").hasRole("CUSTOMER")
                .requestMatchers(HttpMethod.GET, "/api/reviews/get/{bookId}").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/reviews/get/average-rating/**").permitAll()

                .requestMatchers("/api/reports/**").hasRole("ADMIN")

                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
