package com.example.shiftplanner.config;

import com.example.shiftplanner.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF because REST APIs are stateless
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 1. Public Authentication Endpoints and Static Web UI
                        .requestMatchers("/api/auth/**", "/", "/index.html", "/favicon.ico", "/error").permitAll()

                        // 2. Shift Endpoints
                        .requestMatchers(HttpMethod.GET, "/api/shifts/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/shifts/**").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/shifts/**").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/shifts/**").hasRole("MANAGER")

                        // 3. Roster Endpoints
                        .requestMatchers(HttpMethod.GET, "/api/rosters/my").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/rosters/**").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/rosters/**").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/rosters/**").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/rosters/**").hasRole("MANAGER")

                        // 4. Employee Management Endpoints
                        .requestMatchers(HttpMethod.GET, "/api/employees/me").authenticated()
                        .requestMatchers("/api/employees/**").hasRole("MANAGER")

                        // 5. Swap Request Endpoints
                        .requestMatchers(HttpMethod.GET, "/api/swaps").hasRole("MANAGER")
                        .requestMatchers("/api/swaps/**").authenticated()

                        // 6. Any other endpoint requires authentication
                        .anyRequest().authenticated()
                )
                // Use stateless sessions (JWT-based)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // Add JWT filter before UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
