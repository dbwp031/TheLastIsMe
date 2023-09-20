package com.example.thelastisme.auth.handler;

import com.example.thelastisme.auth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.example.thelastisme.config.properties.SecurityProperties;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.naming.AuthenticationException;
import java.io.IOException;

@AllArgsConstructor
@Component
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
    private SecurityProperties securityProperties;


}
