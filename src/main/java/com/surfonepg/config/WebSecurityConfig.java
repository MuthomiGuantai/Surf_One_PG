package com.surfonepg.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .httpBasic(basic -> basic.realmName("Surf One PG"))
            .authorizeHttpRequests(authz -> authz
                // Public endpoints - accessible to everyone
                .requestMatchers(HttpMethod.GET, "/api/v1/packages").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/users").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/payments").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/payments/**").permitAll()
                .requestMatchers("/webhook/**").permitAll()
                .requestMatchers("/health").permitAll()

                // Client endpoints - user can access their own profile
                .requestMatchers(HttpMethod.GET, "/api/v1/users/phone/**").hasAnyRole("CLIENT", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/v1/users/{id}").hasAnyRole("CLIENT", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/users/{id}").hasAnyRole("CLIENT", "ADMIN")

                // Client subscription endpoints
                .requestMatchers(HttpMethod.POST, "/api/v1/subscriptions").hasAnyRole("CLIENT", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/v1/subscriptions/user/{userId}").hasAnyRole("CLIENT", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/v1/subscriptions/user/{userId}/active").hasAnyRole("CLIENT", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/v1/subscriptions/{id}").hasAnyRole("CLIENT", "ADMIN")

                // Admin only endpoints - users management
                .requestMatchers(HttpMethod.GET, "/api/v1/users").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/v1/users/active/list").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/users/{id}").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/users/{id}/deactivate").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/users/{id}/activate").hasRole("ADMIN")

                // Admin only endpoints - subscriptions management
                .requestMatchers(HttpMethod.GET, "/api/v1/subscriptions").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/v1/subscriptions/package/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/v1/subscriptions/status/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/subscriptions/{id}/activate").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/subscriptions/{id}/expire").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/subscriptions/{id}/cancel").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/v1/subscriptions/expire-expired").hasRole("ADMIN")

                // Admin only endpoints - renewals
                .requestMatchers(HttpMethod.GET, "/api/v1/renewals").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/v1/renewals/**").hasRole("ADMIN")

                // All other requests require authentication
                .anyRequest().authenticated()
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",
            "http://localhost:8080",
            "http://localhost:5173",
            "*"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}





