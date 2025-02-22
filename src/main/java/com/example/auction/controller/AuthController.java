package com.example.auction.controller;

import com.example.auction.model.User;
import com.example.auction.service.TokenService;
import com.example.auction.service.UserService;

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
        if (loggedInUser != null) {
            String token = tokenService.generateToken(loggedInUser.getEmail());
            return ResponseEntity.ok().body(token);
        }
        return ResponseEntity.status(401).body("E-posta veya şifre hatalı.");
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user, @RequestParam String cardToken) {
        try {
            userService.registerUser(user, cardToken);
            return ResponseEntity.ok().body("Kayıt başarılı!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Bir hata oluştu: " + e.getMessage());
        }
    }

    @GetMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        boolean isValid = tokenService.validateToken(token);
        return ResponseEntity.ok().body(isValid ? "Geçerli token." : "Geçersiz token.");
    }
}

