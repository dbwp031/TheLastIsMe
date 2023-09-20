package com.example.thelastisme.config;

import com.example.thelastisme.auth.filter.JwtAuthFilter;
import com.example.thelastisme.auth.filter.JwtExceptionInterceptorFilter;
import com.example.thelastisme.auth.handler.CustomAuthenticationEntryPoint;
import com.example.thelastisme.auth.handler.JWTFailureHandler;
import com.example.thelastisme.auth.handler.OAuth2SuccessHandler;
import com.example.thelastisme.auth.provider.JwtProvider;
import com.example.thelastisme.auth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.example.thelastisme.auth.service.CustomOAuth2UserService;
import com.example.thelastisme.auth.service.TokenService;
import com.example.thelastisme.config.properties.SecurityProperties;
import com.example.thelastisme.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final TokenService tokenService;
    private final MemberRepository memberRepository;
    private final SecurityProperties securityProperties;
    private final AuthenticationConfiguration authenticationConfiguration;

    @Bean
    OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    @Bean
    JwtAuthFilter jwtAuthFilter() throws Exception {
        return new JwtAuthFilter(authenticationManager(authenticationConfiguration), new JWTFailureHandler());
    }
    @Bean
    CustomAuthenticationEntryPoint customAuthenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint();
    }
    @Bean
    JwtExceptionInterceptorFilter jwtExceptionInterceptorFilter() {
        return new JwtExceptionInterceptorFilter(customAuthenticationEntryPoint());
    }
    @Bean
    OAuth2SuccessHandler oAuth2SuccessHandler() {
        return new OAuth2SuccessHandler(tokenService, memberRepository, oAuth2AuthorizationRequestBasedOnCookieRepository(), securityProperties);
    }
}
