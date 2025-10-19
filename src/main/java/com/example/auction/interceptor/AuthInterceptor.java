package com.example.auction.interceptor;

import com.example.auction.service.TokenService;
import com.example.auction.service.UserService;
import com.example.auction.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import com.example.auction.common.message.MessageCode;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final TokenService tokenService;
    private final UserService userService;

    public AuthInterceptor(TokenService tokenService, UserService userService) {
        this.tokenService = tokenService;
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");

        if (!tokenService.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(MessageCode.INVALID_TOKEN.getMessage());
            return false;
        }

        String email = tokenService.getEmailFromToken(token);
        User user = userService.findUserByEmail(email);
        request.setAttribute("authenticatedUser", user);

        return true;
    }
}
