package com.gov.cmr.transparisation_module.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtTokenProvider jwtTokenProvider;

    public SecurityConfig(
            CustomUserDetailsService customUserDetailsService,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            JwtTokenProvider jwtTokenProvider
    ) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // 1) JwtAuthenticationFilter as a Bean
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider, customUserDetailsService);
    }

    // 2) Security filter chain
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF (token-based auth)
                .csrf(csrf -> csrf.disable())

                // Custom entry point for unauthorized
                .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))

                // Stateless session
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // Let preflight requests in
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Let /api/auth/** be open
                        .requestMatchers("/api/auth/**").permitAll()

                        .requestMatchers("/api/categories/**").permitAll()
                        .requestMatchers("/api/referentiel-titres/**").permitAll()
                        .requestMatchers("/api/transparisation/**").permitAll()
                        .requestMatchers("/api/op/**").permitAll()
                        // Let POST /api/users be open if you do sign-up
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()

                        // **Add** a rule for /api/fiche-portefeuille/upload
                        .requestMatchers(HttpMethod.POST, "/api/fiche-portefeuille/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/fiche-portefeuille/**").permitAll()

                        // Restrict everything else
                        .anyRequest().authenticated()
                )


                // Add JWT filter
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 3) DaoAuthenticationProvider
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // 4) AuthenticationManager for login
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig)
            throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // 5) BCrypt for password hashing
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
