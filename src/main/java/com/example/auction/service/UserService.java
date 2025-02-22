package com.example.auction.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (BCrypt.checkpw(password, user.getPassword())) {
                return user;
            }
        }
        return null;
    }

    public void registerUser(User user, String cardToken) throws StripeException {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Bu e-posta adresi zaten kayıtlı.");
        }

        String stripeCustomerId = stripeService.createStripeCustomer(user.getEmail(), cardToken);
        
        user.setStripeCustomerId(stripeCustomerId);

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
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Bu e-posta adresine sahip bir kullanıcı bulunamadı."));
    }
}
