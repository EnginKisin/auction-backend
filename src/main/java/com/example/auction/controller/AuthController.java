package com.example.auction.controller;

import com.example.auction.common.response.ResponseHandler;
import com.example.auction.model.User;
import com.example.auction.service.TokenService;
import com.example.auction.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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

    public AuthController(UserService userService, TokenService tokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user) {
        User loggedInUser = userService.validateUser(user.getEmail(), user.getPassword());
        String token = tokenService.generateToken(loggedInUser.getEmail());
        return ResponseHandler.success(token, "Token başarıyla alındı.", HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user, @RequestParam String cardToken) {
        userService.registerUser(user, cardToken);
        return ResponseHandler.success(null, "Kullanıcı başarıyla kaydedildi.", HttpStatus.OK);
    }

    @GetMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        boolean isValid = tokenService.validateToken(token);
        return ResponseHandler.success(null, isValid ? "Geçerli token." : "Geçersiz token.", HttpStatus.OK);
    }
}
