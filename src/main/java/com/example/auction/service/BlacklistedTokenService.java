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
        String jwtToken = token.substring(7);
        BlacklistedToken blacklistedToken = new BlacklistedToken();
        blacklistedToken.setToken(jwtToken);
        return blacklistedTokenRepository.save(blacklistedToken);
    }

    public boolean isTokenBlacklisted(String token) {
        String jwtToken = token.substring(7);
        return blacklistedTokenRepository.existsByToken(jwtToken);
    }
}
