package com.neobank.config;
 
import com.neobank.security.JwtAuthenticationFilter;
 
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;
import jakarta.servlet.http.HttpServletResponse;
 
import java.util.Arrays;
 
@Configuration
@EnableMethodSecurity
public class SecurityConfig {
 
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
 
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }
 
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
 
        http
            .csrf(csrf -> csrf.disable())
 
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
 
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
 
            .exceptionHandling(ex ->
                ex.authenticationEntryPoint(unauthorizedEntryPoint())
            )
 
            .authorizeHttpRequests(auth -> auth
 
                /* ✅ PUBLIC APIs */
                .requestMatchers(
                    "/api/auth/register",
                    "/api/auth/login",
                    "/swagger-ui/**",
                    "/v3/api-docs/**"
                ).permitAll()
 
                /* Loan products */
                .requestMatchers(HttpMethod.GET, "/api/loans/products", "/api/loans/products/*")
                .authenticated()
 
                .requestMatchers(HttpMethod.POST, "/api/loans/products")
                .hasAuthority("ADMIN")
 
                /* Customer loan actions */
                .requestMatchers("/api/loans/apply")
                .hasAuthority("CUSTOMER")
 
                .requestMatchers(
                    "/api/loans/my",
                    "/api/loans/my-applications",
                    "/api/loans/my-accounts",
                    "/api/loans/*/repayments",
                    "/api/loans/*/repayments/*/pay"
                )
                .hasAnyAuthority("CUSTOMER", "ADMIN")
 
                /* Admin loan APIs */
                .requestMatchers(
                    "/api/loans/admin",
                    "/api/loans/admin/applications"
                )
                .hasAuthority("ADMIN")
 
                .requestMatchers("/api/loans/*/decision")
                .hasAuthority("ADMIN")
 
                /* ✅ ADMIN MODULE */
                .requestMatchers("/api/admin/**")
                .hasAuthority("ADMIN")
 
                /* ✅ CUSTOMER MODULE */
                .requestMatchers("/api/customer/**")
                .hasAnyAuthority("CUSTOMER", "ADMIN")
 
                /* ✅ OTHER MODULES (LOGIN REQUIRED) */
                .requestMatchers("/api/accounts/**").authenticated()
                .requestMatchers("/api/transactions/**").authenticated()
                .requestMatchers("/api/insights/**").authenticated()
                .requestMatchers("/api/budgets/**").authenticated()
                .requestMatchers("/api/bills/**").authenticated()
                .requestMatchers("/api/rewards/**").authenticated()
 
                .anyRequest().authenticated()
            )
 
            .httpBasic(httpBasic -> httpBasic.disable())
            .formLogin(form -> form.disable())
 
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );
 
        return http.build();
    }
 
    @Bean
    public AuthenticationEntryPoint unauthorizedEntryPoint() {
        return (request, response, authException) ->
            response.sendError(
                HttpServletResponse.SC_UNAUTHORIZED,
                "Unauthorized"
            );
    }
 
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
 
    /* ✅ ✅ ✅ FINAL CORS FIX */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
 
        CorsConfiguration config = new CorsConfiguration();
 
        config.setAllowedOrigins(Arrays.asList(
            "http://localhost:4200"
        ));
 
        config.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));
 
        config.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type"
        ));
 
        config.setAllowCredentials(true);
 
        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();
 
        source.registerCorsConfiguration("/**", config);
 
        return source;
    }
}
 