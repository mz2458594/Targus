package com.example.domain.ecommerce.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.example.domain.ecommerce.config.JwtFilter;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/category/**").hasAnyRole("Administrador", "Empleado")
                        .requestMatchers("/api/control/**").hasAnyRole("Administrador", "Empleado")
                        .requestMatchers("/api/order/**").hasAnyRole("Administrador", "Empleado", "Cliente")
                        .requestMatchers("/api/person/**").hasAnyRole("Administrador", "Cliente")
                        .requestMatchers("/api/products/**").hasAnyRole("Administrador", "Cliente", "Empleado")
                        .requestMatchers("/api/provider/**").hasAnyRole("Administrador", "Empleado")
                        .requestMatchers("/api/sale/**").hasAnyRole("Administrador", "Empleado", "Cliente")
                        .requestMatchers("/api/user/**").hasAnyRole("Administrador", "Empleado", "Cliente")
                        .anyRequest().authenticated())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        ;
        return http.build();
    }

}
