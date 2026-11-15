
package com.danielchavarri.taskflow.shared.infrastructure.security;

import java.util.List;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.beans.factory.annotation.Value;
import java.util.Arrays;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;

        public SecurityConfig(
                        JwtAuthenticationFilter jwtAuthenticationFilter) {
                this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource(
                        @Value("${taskflow.cors.allowed-origins}") String allowedOrigins) {

                CorsConfiguration configuration = new CorsConfiguration();

                List<String> origins = Arrays.stream(allowedOrigins.split(","))
                                .map(String::trim)
                                .filter(origin -> !origin.isBlank())
                                .toList();

                if (origins.isEmpty()) {
                        throw new IllegalStateException(
                                        "CORS_ALLOWED_ORIGINS debe contener al menos un origen válido.");
                }

                configuration.setAllowedOrigins(origins);

                configuration.setAllowedMethods(
                                List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

                configuration.setAllowedHeaders(
                                List.of("Authorization", "Content-Type"));

                configuration.setAllowCredentials(false);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

                source.registerCorsConfiguration("/**", configuration);

                return source;
        }

        @Bean
        public AccessDeniedHandler accessDeniedHandler() {
                return (request, response, exception) -> {
                        response.setStatus(
                                        HttpServletResponse.SC_FORBIDDEN);

                        response.setContentType("application/problem+json");
                        response.setCharacterEncoding("UTF-8");

                        String body = """
                                        {
                                          "type": "https://api.taskflow.local/problems/403",
                                          "title": "Access denied",
                                          "status": 403,
                                          "detail": "No tienes permisos para realizar esta operación."
                                        }
                                        """;

                        response.getWriter().write(body);
                };
        }

        @Bean
        public SecurityFilterChain securityFilterChain(
                        HttpSecurity http,
                        AccessDeniedHandler accessDeniedHandler,
                        RestAuthenticationEntryPoint authenticationEntryPoint)
                        throws Exception {

                http
                                .cors(cors -> {
                                })
                                .csrf(AbstractHttpConfigurer::disable)
                                .httpBasic(AbstractHttpConfigurer::disable)
                                .formLogin(AbstractHttpConfigurer::disable)
                                .logout(AbstractHttpConfigurer::disable)

                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(
                                                                SessionCreationPolicy.STATELESS))

                                .authorizeHttpRequests(auth -> auth

                                                // 1. Permitir solicitudes CORS preflight.
                                                .requestMatchers(
                                                                HttpMethod.OPTIONS,
                                                                "/**")
                                                .permitAll()

                                                // 2. Endpoints públicos de autenticación y documentación.
                                                .requestMatchers(
                                                                "/api/auth/register",
                                                                "/api/auth/login",
                                                                "/swagger-ui/**",
                                                                "/swagger-ui.html",
                                                                "/v3/api-docs/**")
                                                .permitAll()

                                                // 3. ADMIN: intervención directa sobre el estado.
                                                .requestMatchers(
                                                                HttpMethod.PATCH,
                                                                "/api/tasks/*/admin-intervention")
                                                .hasRole("ADMIN")

                                                // 4. ADMIN: aprobar solicitudes de finalización.
                                                .requestMatchers(
                                                                HttpMethod.PATCH,
                                                                "/api/tasks/*/approve-completion")
                                                .hasRole("ADMIN")

                                                // 5. ADMIN: rechazar solicitudes de finalización.
                                                .requestMatchers(
                                                                HttpMethod.PATCH,
                                                                "/api/tasks/*/reject-completion")
                                                .hasRole("ADMIN")

                                                // 6. ADMIN: consultar el historial de intervenciones.
                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/tasks/*/admin-interventions")
                                                .hasRole("ADMIN")

                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/tasks/admin/pending-review")
                                                .hasRole("ADMIN")

                                                // 7. Cualquier otra operación requiere autenticación.
                                                .anyRequest().authenticated())

                                .exceptionHandling(exceptions -> exceptions
                                                .authenticationEntryPoint(authenticationEntryPoint)
                                                .accessDeniedHandler(accessDeniedHandler))

                                .addFilterBefore(
                                                jwtAuthenticationFilter,
                                                UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        public FilterRegistrationBean<JwtAuthenticationFilter> jwtAuthenticationFilterRegistration(
                        JwtAuthenticationFilter filter) {

                FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>(filter);

                registration.setEnabled(false);

                return registration;
        }
}