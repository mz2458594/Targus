package com.example.domain.ecommerce.config;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Base64.Decoder;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtUtil {

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private int expiration;

    @Value("${jwt.refresh}")
    private int refresh;

    private Key key;

    @PostConstruct
    public void init() {
        // Byte[] bytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String generateToken(Authentication authentication) {
        return Jwts.builder()
                .setSubject(authentication.getName())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .claim("authorities",
                        authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(Authentication authentication) {

        return Jwts.builder()
                .setSubject(authentication.getName())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refresh))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public List<GrantedAuthority> getAuthorities(String token) {
        List<String> roles = verifiedClaim(token).get("authorities", List.class);

        List<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role))
                .collect(Collectors.toList());

        return authorities;

    }

    public String getEmail(String token) {
        return verifiedClaim(token).getSubject();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        Claims claims = verifiedClaim(token);

        final String emailToken = getEmail(token);

        boolean isExpired = claims.getExpiration().before(new Date());

        return !isExpired
                && emailToken != null
                && !emailToken.trim().isEmpty()
                && emailToken.equals(userDetails.getUsername());
    }

    public boolean validateRefreshToken(String token) {
        Claims claims = verifiedClaim(token);

        final String emailToken = getEmail(token);

        boolean isExpired = claims.getExpiration().before(new Date());

        return !isExpired
                && emailToken != null
                && !emailToken.trim().isEmpty();
    }

    private Claims verifiedClaim(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key)
                .build().parseClaimsJws(token).getBody();

        if (claims.getExpiration().before(new Date())) {
            throw new JwtException("Expired");
        }

        return claims;
    }

}
