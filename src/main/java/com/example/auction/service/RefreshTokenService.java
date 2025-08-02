package com.example.auction.service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.auction.common.message.MessageCode;
import com.example.auction.model.RefreshToken;
import com.example.auction.repository.RefreshTokenRepository;

@Service
public class RefreshTokenService {

    @Value("${jwt.refresh-token-expiration-ms}")
    private Long refreshTokenExpirationMs;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    public String createRefreshToken(String userEmail) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserEmail(userEmail);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(new Date(System.currentTimeMillis() + refreshTokenExpirationMs));
        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }

    public Optional<RefreshToken> findByToken(String refreshToken) {
        return refreshTokenRepository.findByToken(refreshToken);
    }

    public void deleteByUserEmail(String userEmail) {
        refreshTokenRepository.deleteByUserEmail(userEmail);
    }

    public void validateExpiration(RefreshToken refreshToken) {
        if (refreshToken.getExpiryDate().before(new Date())) {
            throw new IllegalArgumentException(MessageCode.TOKEN_EXPIRED_OR_INVALID.getMessage());
        }
    }
}
