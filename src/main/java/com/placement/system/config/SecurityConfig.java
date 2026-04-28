package com.placement.system.config;

import com.placement.system.security.CustomUserDetailsService;
import com.placement.system.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth

                // ── Public ───────────────────────────────────────────────
            	.requestMatchers("/", "/api/**", "/auth/**").permitAll()
            	.requestMatchers("/api/**").permitAll()
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/api-docs/**", "/swagger-ui.html").permitAll()

                // ── GET /jobs and GET /jobs/** — ALL authenticated roles ──
                // FIX: must be FIRST before any role-restricted rules
                // "/jobs" (exact) + "/jobs/**" (with path segments) both needed
                .requestMatchers(HttpMethod.GET, "/jobs").authenticated()
                .requestMatchers(HttpMethod.GET, "/jobs/**").authenticated()

                // ── Jobs write operations — role restricted ───────────────
                .requestMatchers(HttpMethod.POST, "/jobs").hasAnyRole("EMPLOYER", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/jobs/**").hasAnyRole("EMPLOYER", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/jobs/**").hasAnyRole("EMPLOYER", "PLACEMENT_OFFICER", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/jobs/**").hasAnyRole("EMPLOYER", "ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/jobs/**").hasAnyRole("ADMIN", "PLACEMENT_OFFICER")

                // ── Admin only ───────────────────────────────────────────
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // ── Officer + Admin ──────────────────────────────────────
                .requestMatchers("/officer/**").hasAnyRole("PLACEMENT_OFFICER", "ADMIN")

                // ── Student only ─────────────────────────────────────────
                .requestMatchers("/student/**").hasRole("STUDENT")

                // ── Employer only ────────────────────────────────────────
                .requestMatchers("/employer/**").hasAnyRole("EMPLOYER", "ADMIN")

                // ── Other authenticated endpoints ────────────────────────
                .requestMatchers("/applications/**").authenticated()
                .requestMatchers("/notifications/**").authenticated()
                .requestMatchers("/profile/**").authenticated()

                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // FIXED: use setAllowedOriginPatterns instead of setAllowedOrigins
        // setAllowedOrigins + allowCredentials=true is invalid and breaks preflight
        configuration.setAllowedOriginPatterns(List.of(
            "http://localhost:3000",
            "http://localhost:5173",
            "http://localhost:5174",
            "https://sdp14-frontend.onrender.com"
        ));

        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization", "Content-Type", "Accept", "Origin",
            "Access-Control-Request-Method", "Access-Control-Request-Headers"
        ));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
