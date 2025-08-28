package com.example.domain.ecommerce.services;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;

import com.example.domain.ecommerce.config.JwtUtil;
import com.example.domain.ecommerce.dto.UserDTO;
import com.example.domain.ecommerce.dto.request.AuthenticationRequest;
import com.example.domain.ecommerce.dto.request.RegistrerRequest;
import com.example.domain.ecommerce.dto.response.AuthResponse;
import com.example.domain.ecommerce.models.entities.Persona;
import com.example.domain.ecommerce.models.entities.Usuario;
import com.example.domain.ecommerce.repositories.UsuarioDAO;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthService {

    private final PersonaService personaService;

    private final UsuarioService usuarioService;

    private final UserDetailsService userDetailsService;

    private final UsuarioDAO usuarioDAO;

    private final JwtUtil jwtUtil;

    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegistrerRequest registrerRequest, HttpServletResponse response) {

        Usuario usuario = usuarioService.createUser(registrerRequest);

        personaService.createPersona(registrerRequest, usuario);

        UserDetails userDetails = this.userDetailsService.loadUserByUsername(usuario.getEmail());
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails.getUsername(),
                null,
                userDetails.getAuthorities());

        var jwtToken = jwtUtil.generateToken(authentication);
        var refreshToken = jwtUtil.generateRefreshToken(authentication);

        generateCookie(response, "accessToken", jwtToken, 3600);
        generateCookie(response, "refreshToken", refreshToken, 432000);

        return new AuthResponse(usuario.getIdUsuario(), usuario.getRol().getNombre(), usuario.getEmail());

    }

    public AuthResponse authentication(AuthenticationRequest authenticationRequest, HttpServletResponse response) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authenticationRequest.getEmail(),
                authenticationRequest.getPassword()));

        var jwtToken = jwtUtil.generateToken(authentication);
        var refreshToken = jwtUtil.generateRefreshToken(authentication);

        generateCookie(response, "accessToken", jwtToken, 3600);
        generateCookie(response, "refreshToken", refreshToken, 432000);

        // Puedes agregar para validar el estado
        Usuario usuario = usuarioDAO.findByEmail(authenticationRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado o en estado inactivo"));

        return new AuthResponse(usuario.getIdUsuario(), usuario.getRol().getNombre(), usuario.getEmail());

    }

    public void refresh(HttpServletResponse response, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            throw new RuntimeException("No hay cookies en la petición");
        }

        Optional<String> refreshTokenOpt = Arrays.stream(cookies)
                .filter(c -> c.getName().equals("refreshToken"))
                .map(Cookie::getValue)
                .findFirst();

        String refreshToken = refreshTokenOpt.get();

        if (!jwtUtil.validateRefreshToken(refreshToken)) {
            throw new RuntimeException("Token inválido");
        }

        String username = jwtUtil.getEmail(refreshToken);
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null,
                userDetails.getAuthorities());

        var accessToken = jwtUtil.generateToken(authentication);

        generateCookie(response, "accessToken", accessToken, 3600);

    }

    public Persona update(UserDTO userDTO, int id) {
        Usuario usuario = usuarioService.actualizarUsuarios(userDTO, id);

        return personaService.actualizarPersona(userDTO, usuario);
    }

    public void generateCookie(HttpServletResponse response, String name, String value, int duration) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setAttribute("SameSite", "None");
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(duration);
        response.addCookie(cookie);
    }

    public void deleteCookie(HttpServletResponse response, String name){
        Cookie cookie = new Cookie(name, "");
        cookie.setHttpOnly(true);
        cookie.setAttribute("SameSite", "None");
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

}
