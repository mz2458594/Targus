package com.example.domain.ecommerce.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;

import com.example.domain.ecommerce.config.JwtUtil;
import com.example.domain.ecommerce.dto.UserDTO;
import com.example.domain.ecommerce.dto.request.AuthenticationRequest;
import com.example.domain.ecommerce.dto.request.RegistrerRequest;
import com.example.domain.ecommerce.dto.response.AuthResponse;
import com.example.domain.ecommerce.models.entities.Persona;
import com.example.domain.ecommerce.models.entities.Usuario;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthService {

    private final PersonaService personaService;

    private final UsuarioService usuarioService;

    private final UserDetailsService userDetailsService;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegistrerRequest registrerRequest) {

        Usuario usuario = usuarioService.createUser(registrerRequest);

        personaService.createPersona(registrerRequest, usuario);

        UserDetails userDetails = this.userDetailsService.loadUserByUsername(usuario.getEmail());
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails.getUsername(),
                null,
                userDetails.getAuthorities());

        var jwtToken = jwtUtil.generateToken(authentication);

        return new AuthResponse(jwtToken);

    }

    public AuthResponse authentication(AuthenticationRequest authenticationRequest) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authenticationRequest.getEmail(),
                authenticationRequest.getPassword()));

        var jwToken = jwtUtil.generateToken(authentication);

        return new AuthResponse(jwToken);

    }

    public Persona update(UserDTO userDTO, int id) {
        Usuario usuario = usuarioService.actualizarUsuarios(userDTO, id);

        return personaService.actualizarPersona(userDTO, usuario);
    }
}
