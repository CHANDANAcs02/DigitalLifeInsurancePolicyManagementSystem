package com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            CustomUserDetailsService userDetailsService) {

        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
    }

    // ---------------------------------------------------------
    // PASSWORD ENCODER
    // ---------------------------------------------------------

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ---------------------------------------------------------
    // AUTHENTICATION PROVIDER
    // ---------------------------------------------------------

    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    // ---------------------------------------------------------
    // AUTHENTICATION MANAGER
    // ---------------------------------------------------------

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

    // ---------------------------------------------------------
    // SECURITY FILTER CHAIN
    // ---------------------------------------------------------

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http

                // Disable CSRF because this is a stateless JWT REST API
                .csrf(csrf -> csrf.disable())

                // Enable CORS
                .cors(cors ->
                        cors.configurationSource(corsConfigurationSource()))

                // JWT based application = no HTTP session
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))

                // -------------------------------------------------
                // ERROR HANDLING
                // -------------------------------------------------

                .exceptionHandling(exceptions -> exceptions

                        // 401 - Authentication required
                        .authenticationEntryPoint(
                                (request, response, authException) -> {

                                    response.setStatus(401);
                                    response.setContentType(
                                            MediaType.APPLICATION_JSON_VALUE);

                                    response.getWriter().write(
                                            "{\"status\":401,"
                                                    + "\"error\":\"UNAUTHORIZED\","
                                                    + "\"message\":\"Authentication is required\"}"
                                    );
                                })

                        // 403 - Authenticated but insufficient role
                        .accessDeniedHandler(
                                (request, response, accessDeniedException) -> {

                                    response.setStatus(403);
                                    response.setContentType(
                                            MediaType.APPLICATION_JSON_VALUE);

                                    response.getWriter().write(
                                            "{\"status\":403,"
                                                    + "\"error\":\"FORBIDDEN\","
                                                    + "\"message\":\"You do not have permission to access this resource\"}"
                                    );
                                }))

                // -------------------------------------------------
                // AUTHORIZATION
                // -------------------------------------------------

                .authorizeHttpRequests(auth -> auth

                        // Public APIs
                        .requestMatchers(
                                "/api/auth/register",
                                "/api/auth/login",
                                "/api/health",
                                "/api/plans",
                                "/api/plans/*",
                                "/api/plans/calculate-premium"
                        ).permitAll()

                        // Admin APIs
                        .requestMatchers("/api/admin/**")
                        .hasRole("ADMIN")

                        // Customer APIs
                        .requestMatchers("/api/customers/**")
                        .hasAnyRole(
                                "CUSTOMER",
                                "ADMIN",
                                "EMPLOYEE"
                        )

                        // Policy APIs
                        .requestMatchers("/api/policies/**")
                        .hasAnyRole(
                                "CUSTOMER",
                                "ADMIN",
                                "EMPLOYEE"
                        )

                        // Payment and Premium APIs
                        .requestMatchers(
                                "/api/payments/**",
                                "/api/premiums/**"
                        )
                        .hasAnyRole(
                                "CUSTOMER",
                                "ADMIN",
                                "EMPLOYEE"
                        )

                        // Everything else requires authentication
                        .anyRequest()
                        .authenticated())

                // Use our DAO authentication provider
                .authenticationProvider(authenticationProvider())

                // Run JWT filter before Spring's username/password filter
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    // ---------------------------------------------------------
    // CORS CONFIGURATION
    // ---------------------------------------------------------

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        // Angular frontend
        configuration.setAllowedOrigins(
                List.of("http://localhost:4200")
        );

        // HTTP methods Angular can use
        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "OPTIONS"
                )
        );

        // Headers Angular is allowed to send
        configuration.setAllowedHeaders(
                List.of(
                        "Authorization",
                        "Content-Type",
                        "Accept"
                )
        );

        // Headers browser is allowed to read
        configuration.setExposedHeaders(
                List.of("Authorization")
        );

        // We are using JWT in Authorization header,
        // not browser cookies.
        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }
}