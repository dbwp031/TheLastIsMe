package com.example.thelastisme.auth.provider;

import com.example.thelastisme.auth.authentication.JwtAuthentication;
import com.example.thelastisme.auth.dto.MemberDto;
import com.example.thelastisme.auth.dto.Token;
import com.example.thelastisme.auth.dto.UidDto;
import com.example.thelastisme.auth.enums.AuthProviderType;
import com.example.thelastisme.auth.service.TokenService;
import com.example.thelastisme.domain.Member;
import com.example.thelastisme.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.Optional;
@Slf4j
@Component
public class JwtProvider implements AuthenticationProvider {
    private final MemberService memberService;
    private final TokenService tokenService;
    private final String secretKey;
    private final long tokenPeriod;
    private final long refreshPeriod;

    public JwtProvider(
            @Value("${jwt.secret-key}") String secretKey,
            @Value("${jwt.access-token-validity-in-sec}") long tokenPeriod,
            @Value("${jwt.refresh-token-validity-in-sec}") long refreshPeriod,
            MemberService memberService, TokenService tokenService) {
        this.secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        this.memberService = memberService;
        this.tokenService = tokenService;
        this.tokenPeriod = tokenPeriod * 1000;
        this.refreshPeriod = refreshPeriod * 1000;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("JwtProvider - authenticate: {}", authentication.toString());
        JwtAuthentication jwtAuthenticationToken = (JwtAuthentication) authentication;
        Token token = ((JwtAuthentication) authentication).getToken();
        String accessToken = token.getAccessToken();
        String refreshToken = token.getRefreshToken();

        Optional<String> optionalAccessToken = Optional.ofNullable(accessToken);
        TokenService.JwtCode status = optionalAccessToken.map(this::validateToken)
                .orElse(TokenService.JwtCode.EXPIRED);
        if (status.equals(TokenService.JwtCode.ACCESS)) {
            setAuthMetadata(token, jwtAuthenticationToken);
            return jwtAuthenticationToken;
        } else if (status.equals(TokenService.JwtCode.EXPIRED)) {
            if (refreshToken == null) {
                throw new RuntimeException("토큰 재발급을 위해선 refreshToken이 필요합니다.");
            }
            TokenService.JwtCode code = validateToken(refreshToken);
            Token newToken = tokenService.reissueToken(refreshToken, code);
            setAuthMetadata(newToken, jwtAuthenticationToken);
            return jwtAuthenticationToken;
        } else {
            throw new RuntimeException("JWT Denied");
        }
    }
    @Transactional
    public void setAuthMetadata(Token token, JwtAuthentication authentication) {
        UidDto uidDto = tokenService.getUid(token.getAccessToken());
        String email = uidDto.getEmail();
        AuthProviderType authProviderType = uidDto.getAuthProviderType();
        log.info("email: {}, authProviderType: {}", email, authProviderType);
        Member member = memberService.findByEmailAndAuthProviderType(email, authProviderType).get();
        MemberDto memberDto = MemberDto.builder()
                .email(email)
                .name(member.getName())
                .authProviderType(authProviderType)
                .build();
        authentication.setToken(token);
        authentication.setAuthenticated(true);
        authentication.setPrincipal(memberDto);
        authentication.setPrincipalDetails(member);
        authentication.setAuthorities(member.getRoles());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthentication.class.isAssignableFrom(authentication);
    }

    private TokenService.JwtCode validateToken(String token) {
        return this.tokenService.verifyToken(token);
    }
}
