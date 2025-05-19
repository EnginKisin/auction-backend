package com.example.auction.service;

import java.time.LocalDateTime;
import java.util.Set;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.auction.common.exception.NotFoundException;
import com.example.auction.common.exception.StripeProcessException;
import com.example.auction.common.message.MessageCode;
import com.example.auction.model.Role;
import com.example.auction.model.User;
import com.example.auction.repository.RoleRepository;
import com.example.auction.repository.UserRepository;
import com.stripe.exception.StripeException;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private StripeService stripeService;

    public User validateUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(MessageCode.INVALID_EMAIL.getMessage()));
        
        boolean isPasswordCorrect = BCrypt.checkpw(password, user.getPassword());
        if (!isPasswordCorrect) {
            throw new IllegalArgumentException(MessageCode.INCORRECT_PASSWORD.getMessage());
        }
        return user;
    }

    public String registerUser(User user, String cardToken) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException(MessageCode.EMAIL_ALREADY_REGISTERED.getMessage());
        }

        try {
            String stripeCustomerId = stripeService.createStripeCustomer(user.getEmail(), cardToken);
            user.setStripeCustomerId(stripeCustomerId);
        } catch (StripeException e) {
            throw new StripeProcessException(MessageCode.STRIPE_ERROR.getMessage() + e);
        }

        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        user.setPassword(hashedPassword);

        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            Role defaultRole = roleRepository.findByName("user");
            if (defaultRole != null) {
                user.setRoles(Set.of(defaultRole));
            }
        }

        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);
        return MessageCode.USER_REGISTRATION_SUCCESS.getMessage();
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(MessageCode.USER_NOT_FOUND.getMessage()));
    }
}
