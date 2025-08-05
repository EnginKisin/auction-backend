package com.example.auction.service;

import java.util.Base64;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.auction.common.message.MessageCode;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;

import java.security.Key;

@Service
public class TokenService {

    @Autowired
    private BlacklistedTokenService blacklistedTokenService;

    @Value("${jwt.secret-key}")
    private String secretKeyBase64;

    @Value("${jwt.access-token.expiration-ms}")
    private long accessTokenExpirationMs;

    private Key secretKey;

    @PostConstruct
    public void init() {
        byte[] secretBytes = Base64.getDecoder().decode(secretKeyBase64);
        secretKey = new SecretKeySpec(secretBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    public String generateToken(String email) {

        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException(MessageCode.INVALID_EMAIL.getMessage());
        }

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationMs))
                .signWith(secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return false;
            }

            if (blacklistedTokenService.isTokenBlacklisted(token)) {
                throw new IllegalArgumentException(MessageCode.TOKEN_EXPIRED_OR_INVALID.getMessage());
            }

            String jwtToken = token.substring(7);
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(jwtToken)
                    .getBody();

            Date expiration = claims.getExpiration();
            return expiration.after(new Date());
        } catch (Exception e) {
            throw new IllegalArgumentException(MessageCode.TOKEN_EXPIRED_OR_INVALID.getMessage());
        }
    }

    public String getEmailFromToken(String token) {
        try {
            String jwtToken = token.substring(7);
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(jwtToken)
                    .getBody();
            return claims.getSubject();
        } catch (Exception e) {
            throw new IllegalArgumentException(MessageCode.EMAIL_NOT_FOUND_IN_TOKEN.getMessage());
        }
    }
}
