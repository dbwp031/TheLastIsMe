package com.example.thelastisme.config;

import com.example.thelastisme.auth.filter.JwtAuthFilter;
import com.example.thelastisme.auth.filter.JwtExceptionInterceptorFilter;
import com.example.thelastisme.auth.handler.CustomAuthenticationEntryPoint;
import com.example.thelastisme.auth.handler.OAuth2SuccessHandler;
import com.example.thelastisme.auth.provider.JwtProvider;
import com.example.thelastisme.auth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.example.thelastisme.auth.service.CustomOAuth2UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
@RequiredArgsConstructor
@Configuration
public class AuthConfig {
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final JwtAuthFilter jwtAuthFilter;
    private final JwtProvider jwtProvider;
    private final JwtExceptionInterceptorFilter jwtExceptionInterceptorFilter;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final SecurityConfig securityConfig;


    private static CustomOAuth2UserService staticCustomOAuth2UserService;
    private static JwtProvider staticJwtProvider;
    private static OAuth2SuccessHandler staticOAuth2SuccessHandler;
    private static OAuth2AuthorizationRequestBasedOnCookieRepository staticOAuth2AuthorizationRequestBasedOnCookieRepository;
    private static CustomAuthenticationEntryPoint staticCustomAuthenticationEntryPoint;
    //    private static JwtExceptionInterceptorFilter staticJwtExceptionInterceptorFilter;
    private static SecurityConfig staticSecurityConfig;
    private static ClientRegistrationRepository staticClientRegistrationRepository;

    @PostConstruct
    public void init() {
        staticCustomOAuth2UserService = this.customOAuth2UserService;
        staticJwtProvider = this.jwtProvider;
        staticOAuth2SuccessHandler = this.oAuth2SuccessHandler;
        staticOAuth2AuthorizationRequestBasedOnCookieRepository = this.oAuth2AuthorizationRequestBasedOnCookieRepository;
        staticCustomAuthenticationEntryPoint = this.customAuthenticationEntryPoint;
//        staticJwtExceptionInterceptorFilter = this.jwtExceptionInterceptorFilter;
        staticSecurityConfig = this.securityConfig;
        staticClientRegistrationRepository = this.clientRegistrationRepository;

    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.
                cors(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .rememberMe(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/oauth2/**").permitAll()
                .anyRequest().authenticated());

        http.addFilterAt(staticSecurityConfig.jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(staticSecurityConfig.jwtExceptionInterceptorFilter(), JwtAuthFilter.class);
        http.authenticationProvider(staticJwtProvider);

        http.oauth2Login(oauth -> oauth

                        .authorizationEndpoint(auth -> auth
                                .baseUri("/oauth2/authorize")
                                .authorizationRequestRepository(staticOAuth2AuthorizationRequestBasedOnCookieRepository))
                        .clientRegistrationRepository(staticClientRegistrationRepository)
                        .userInfoEndpoint(userInfo -> userInfo.userService(staticCustomOAuth2UserService))
                        .successHandler(staticOAuth2SuccessHandler)
//                .failureHandler()
        );

        http.logout(logout -> logout
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID"));


        return http.build();
    }
}
