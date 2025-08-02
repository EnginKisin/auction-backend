package com.example.auction.controller;

import com.example.auction.common.message.MessageCode;
import com.example.auction.common.response.ResponseHandler;
import com.example.auction.model.User;
import com.example.auction.service.BlacklistedTokenService;
import com.example.auction.service.RefreshTokenService;
import com.example.auction.service.TokenService;
import com.example.auction.service.UserService;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final TokenService tokenService;
    private final RefreshTokenService refreshTokenService;
    private final BlacklistedTokenService blacklistedTokenService;

    public AuthController(UserService userService, TokenService tokenService, RefreshTokenService refreshTokenService, BlacklistedTokenService blacklistedTokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
        this.refreshTokenService = refreshTokenService;
        this.blacklistedTokenService = blacklistedTokenService;
    }

    // @PostMapping("/login")
    // public ResponseEntity<?> loginUser(@RequestBody User user) {
    //     User loggedInUser = userService.validateUser(user.getEmail(), user.getPassword());
    //     String token = tokenService.generateToken(loggedInUser.getEmail());
    //     return ResponseHandler.success(token, MessageCode.TOKEN_SUCCESS.getMessage(), HttpStatus.OK);
    // }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user) {
        User loggedInUser = userService.validateUser(user.getEmail(), user.getPassword());
        String accessToken = tokenService.generateToken(loggedInUser.getEmail());
        String refreshToken = refreshTokenService.createRefreshToken(loggedInUser.getEmail());
        return ResponseHandler.success(Map.of("accessToken", accessToken, "refreshToken", refreshToken),  MessageCode.TOKEN_SUCCESS.getMessage(), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user, @RequestParam String cardToken) {
        userService.registerUser(user, cardToken);
        return ResponseHandler.success(null, MessageCode.USER_REGISTRATION_SUCCESS.getMessage(), HttpStatus.OK);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshAccessToken(@RequestParam String refreshToken) {
        return refreshTokenService.findByToken(refreshToken)
                .map(token -> {
                    refreshTokenService.validateExpiration(token);
                    String newAccessToken = tokenService.generateToken(token.getUserEmail());
                    return ResponseHandler.success(
                        Map.of("accessToken", newAccessToken),
                        MessageCode.ACCESS_TOKEN_REFRESHED.getMessage(),
                        HttpStatus.OK
                    );
                })
                .orElse(ResponseHandler.error(
                    MessageCode.TOKEN_EXPIRED_OR_INVALID.getMessage(),
                    HttpStatus.UNAUTHORIZED
                ));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);

            try {
                if (tokenService.validateToken(jwtToken)) {
                    blacklistedTokenService.blacklistToken(jwtToken);
                    String email = tokenService.getEmailFromToken(jwtToken);
                    refreshTokenService.deleteByUserEmail(email);
                }
            } catch (Exception e) {
            //
            }
        }

        return ResponseHandler.success(null, MessageCode.USER_LOGOUT_SUCCESS.getMessage(), HttpStatus.OK);
    }

}
