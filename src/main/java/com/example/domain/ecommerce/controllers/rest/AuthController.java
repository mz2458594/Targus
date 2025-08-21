package com.example.domain.ecommerce.controllers.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.domain.ecommerce.dto.request.AuthenticationRequest;
import com.example.domain.ecommerce.dto.request.RegistrerRequest;
import com.example.domain.ecommerce.dto.response.AuthResponse;
import com.example.domain.ecommerce.services.AuthService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/registrer")
    public ResponseEntity<AuthResponse> registrer(@RequestBody RegistrerRequest request){
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/authentication")
    public ResponseEntity<AuthResponse> authentication(@RequestBody AuthenticationRequest request){
        return ResponseEntity.ok(authService.authentication(request));
    }


}
