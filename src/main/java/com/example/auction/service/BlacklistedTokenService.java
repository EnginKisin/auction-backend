package com.example.auction.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.auction.model.BlacklistedToken;
import com.example.auction.repository.BlacklistedTokenRepository;

@Service
public class BlacklistedTokenService {
    
    @Autowired
    private BlacklistedTokenRepository blacklistedTokenRepository;

    public BlacklistedToken blacklistToken(String token) {
        BlacklistedToken blacklistedToken = new BlacklistedToken();
        blacklistedToken.setToken(token);
        return blacklistedTokenRepository.save(blacklistedToken);
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokenRepository.existsByToken(token);
    }
}
