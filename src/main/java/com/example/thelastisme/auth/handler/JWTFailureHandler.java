package com.example.thelastisme.auth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Component
public class JWTFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        RuntimeException runtimeException = new RuntimeException("jwt runtime exception");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        Cookie accessToken = getCookieValue(request, "accessToken").orElse(new Cookie("accessToken", null));
        Cookie refreshToken = getCookieValue(request, "refreshToken").orElse(new Cookie("refreshToken", null));
        accessToken.setMaxAge(0);
        refreshToken.setMaxAge(0);
        response.addCookie(accessToken);
        response.addCookie(refreshToken);

    }
    private Optional<Cookie> getCookieValue(HttpServletRequest req, String cookieName) {
        if (req.getCookies() == null) return null;
        return Arrays.stream(req.getCookies())
                .filter(c -> c.getName().equals(cookieName))
                .findFirst();
    }
}
