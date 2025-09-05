package com.example.domain.ecommerce.controllers.rest;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.domain.ecommerce.dto.request.AuthenticationRequest;
import com.example.domain.ecommerce.dto.request.RegistrerRequest;
import com.example.domain.ecommerce.dto.response.AuthResponse;
import com.example.domain.ecommerce.services.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/registrer")
    public ResponseEntity<AuthResponse> registrer(@RequestBody RegistrerRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(authService.register(request, response));
    }

    @PostMapping("/authentication")
    public ResponseEntity<AuthResponse> authentication(@RequestBody AuthenticationRequest request,
            HttpServletResponse response) {
        return ResponseEntity.ok(authService.authentication(request, response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletResponse response, HttpServletRequest request) {
        try {
            authService.refresh(response, request);
            return ResponseEntity.ok("Token refrescado");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("Error", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response){
        authService.deleteCookie(response, "accessToken");
        authService.deleteCookie(response, "refreshToken");
        return ResponseEntity.noContent().build();
    }

}
