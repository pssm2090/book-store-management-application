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

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                
            	.requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
            	.requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
            	.requestMatchers(HttpMethod.POST, "/api/auth/refresh-token").permitAll()
            	    
                .requestMatchers(HttpMethod.GET, "/api/books/get/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/books/get-all").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/books/search").permitAll()
                
                .requestMatchers(HttpMethod.GET, "/api/categories/get/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/categories/get-all").permitAll()
                
                .requestMatchers(HttpMethod.GET, "/api/reviews/get/{bookId}").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/reviews/get/average-rating/**").permitAll()


//                
//                .requestMatchers("/api/cart/**").hasRole("CUSTOMER")
//
//                .requestMatchers(HttpMethod.POST, "/api/orders/**").hasRole("CUSTOMER")   
//                .requestMatchers(HttpMethod.GET, "/api/orders/**").authenticated()        
//                .requestMatchers(HttpMethod.PUT, "/api/orders/**").hasRole("ADMIN")       
//                .requestMatchers(HttpMethod.DELETE, "/api/orders/**").authenticated()    // handling with condition
//                
//                .requestMatchers(HttpMethod.POST, "/api/returns/**").hasRole("CUSTOMER")
//                .requestMatchers(HttpMethod.GET, "/api/returns/my").hasRole("CUSTOMER")
//                .requestMatchers(HttpMethod.GET, "/api/returns").hasRole("ADMIN")
//                .requestMatchers(HttpMethod.POST, "/api/returns/*/process").hasRole("ADMIN")
//



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
