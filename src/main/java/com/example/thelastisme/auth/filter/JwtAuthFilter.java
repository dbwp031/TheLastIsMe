package com.example.thelastisme.auth.filter;

import com.example.thelastisme.auth.authentication.JwtAuthentication;
import com.example.thelastisme.auth.dto.Token;
import com.example.thelastisme.auth.exception.JwtAuthenticationException;
import com.example.thelastisme.auth.handler.JWTFailureHandler;
import com.example.thelastisme.exception.ErrorCode;
import com.example.thelastisme.util.CookieUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

/*
* 발급받은 토큰을 이용하여 security 인증을 처리하는 필터
* */
@RequiredArgsConstructor
    public class JwtAuthFilter extends OncePerRequestFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTFailureHandler failureHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = getCookieValue(request, "accessToken");
        String refreshToken = getCookieValue(request, "refreshToken");
        if (accessToken == null && refreshToken == null) {
            filterChain.doFilter(request, response);
        } else {
            try {
                Token jwtToken = new Token(accessToken, refreshToken);
                JwtAuthentication authenticationRequest = new JwtAuthentication(jwtToken, request, response);
                JwtAuthentication authenticationResult = (JwtAuthentication) this.authenticationManager.authenticate(authenticationRequest);
                SecurityContextHolder.getContext().setAuthentication(authenticationResult);
                postAuthenticate(request, response, authenticationResult);
            } catch (JwtAuthenticationException ex) {
                SecurityContextHolder.clearContext();
                this.failureHandler.onAuthenticationFailure(request, response, ex);
                throw ex;
            } catch (Exception ex) {
                SecurityContextHolder.clearContext();
                JwtAuthenticationException authenticationException = new JwtAuthenticationException("jwt인증에 실패했습니다", ex.getCause(), ErrorCode.JWT_BAD_REQUEST);
                this.failureHandler.onAuthenticationFailure(request, response, authenticationException);
                throw authenticationException;
            }
            filterChain.doFilter(request, response);
        }
    }

    private void postAuthenticate(HttpServletRequest request, HttpServletResponse response, JwtAuthentication authenticationResult) {
        JwtAuthentication jwtAuthenticationResult = (JwtAuthentication) authenticationResult;
        ResponseCookie accessTokenCookie = CookieUtil.createAccessTokenCookie(jwtAuthenticationResult.getToken().getAccessToken());
        ResponseCookie refreshTokenCookie = CookieUtil.createAccessTokenCookie(jwtAuthenticationResult.getToken().getRefreshToken());
        response.addHeader("Set-Cookie",accessTokenCookie.toString());
        response.addHeader("Set-Cookie",refreshTokenCookie.toString());
    }

    private String getCookieValue(HttpServletRequest request, String cookieName) {
        if(request.getCookies() == null) return null;

        return Arrays.stream(request.getCookies())
                .filter(c -> c.getName().equals(cookieName))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);

    }
}
