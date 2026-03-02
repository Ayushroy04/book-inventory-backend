package com.example.book_inventory.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthnFilter;
        private final UserDetailsService userDetailsService;
        private final PasswordEncoder passwordEncoder;

        @Bean
        public AuthenticationProvider authenticationProvider() {
                DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
                provider.setUserDetailsService(userDetailsService);
                provider.setPasswordEncoder(passwordEncoder);
                return provider;
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
                return config.getAuthenticationManager();
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter)
                        throws Exception {

                http
                                .csrf(csrf -> csrf.disable())
                                // Fix: Use STATELESS instead of disable() for JWT-based authentication
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                // Add security headers
                                .headers(headers -> headers
                                                // XSS Protection
                                                .xssProtection(xss -> xss
                                                                .headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
                                                // Content Security Policy
                                                .contentSecurityPolicy(csp -> csp
                                                                .policyDirectives(
                                                                                "default-src 'self'; frame-ancestors 'none';"))
                                                // Prevent clickjacking
                                                .frameOptions(frame -> frame.deny())
                                                // Prevent MIME type sniffing
                                                .contentTypeOptions(contentType -> {
                                                }))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/auth/**").permitAll()
                                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                                // Allow public access to book browsing and reviews/ratings
                                                .requestMatchers(org.springframework.http.HttpMethod.GET, "/books/**")
                                                .permitAll()
                                                // POST reviews and DELETE reviews require authentication (handled
                                                // below)
                                                .requestMatchers(org.springframework.http.HttpMethod.POST,
                                                                "/books/*/reviews")
                                                .authenticated()
                                                .requestMatchers(org.springframework.http.HttpMethod.DELETE,
                                                                "/reviews/**")
                                                .authenticated()
                                                .anyRequest().authenticated())
                                .authenticationProvider(authenticationProvider())
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();

        }
}
