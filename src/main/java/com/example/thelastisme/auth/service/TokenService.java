package com.example.thelastisme.auth.service;

import com.example.thelastisme.auth.dto.Token;
import com.example.thelastisme.auth.dto.UidDto;
import com.example.thelastisme.auth.enums.AuthProviderType;
import com.example.thelastisme.auth.enums.MemberRole;
import com.example.thelastisme.domain.Member;
import com.example.thelastisme.service.MemberService;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.Date;


@Slf4j
@Service
public class TokenService {
    public static enum JwtCode {
        DENIED,
        ACCESS,
        EXPIRED;
    }

    private String secretKey;
    private final Long tokenPeriod;
    private final Long refreshPeriod;
    private final MemberService memberService;

    public TokenService(
            @Value("${jwt.secret-key}") String secretKey,
            @Value("${jwt.access-token-validity-in-sec}") long tokenPeriod,
            @Value("${jwt.refresh-token-validity-in-sec}") long refreshPeriod,
            MemberService memberService) {
                this.secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        this.tokenPeriod = tokenPeriod * 1000;
        this.refreshPeriod = refreshPeriod * 1000;
        this.memberService = memberService;
    }

    public Token generateToken(String email, AuthProviderType authProviderType, MemberRole role) {
        log.info("GENERATE TOKEN - EMAIL: {}, AUTHPROVIDERTYPE: {}, ROLE: {}", email, authProviderType, role);
        String subject = email + "," + authProviderType.name();

        Claims claims = Jwts.claims().setSubject(subject);
        claims.put("role", role.getAuthority());

        Date now = new Date();
        return new Token(this.generateAccessToken(email, authProviderType, role),
                this.generateRefreshToken(email, authProviderType, role));
    }
    private String generateAccessToken(String email, AuthProviderType authProviderType, MemberRole role) {
        String subject = email + "," + authProviderType.name();

        Claims claims = Jwts.claims().setSubject(subject);
        claims.put("role", role.getAuthority());

        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenPeriod))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    private String generateRefreshToken(String email, AuthProviderType authProviderType, MemberRole role) {
        String subject = email + "," + authProviderType.name();

        Claims claims = Jwts.claims().setSubject(subject);
        claims.put("role", role.getAuthority());

        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshPeriod))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public JwtCode verifyToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token);
            if (claims.getBody()
                    .getExpiration()
                    .before(new Date())) {
                throw new ExpiredJwtException(claims.getHeader(), claims.getBody(), "expired Token, reissue refresh Token");
            }
            return JwtCode.ACCESS;
        }catch (ExpiredJwtException e) {
            return JwtCode.EXPIRED;
        } catch (Exception e) {
            return JwtCode.DENIED;
        }
    }
    /*
    * @return email|authProviderType 형식의 문자열 ex) hello@naver.com|NAVER
    * */
    public UidDto getUid(String token) {
        String subject = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();

        return UidDto.builder()
                .email(subject.split(",")[0])
                .authProviderType(AuthProviderType.valueOf(subject.split(",")[1]))
                .build();
    }

    @Transactional
    public Token reissueToken(String refreshToken) throws RuntimeException {
        UidDto uidDto = getUid(refreshToken);
        String email = uidDto.getEmail();
        AuthProviderType authProviderType = uidDto.getAuthProviderType();
        Member member = memberService.findByEmailAndAuthProviderType(email, authProviderType).get();
        if (member.getRefreshToken().equals(refreshToken)) {
            Token newToken = generateToken(email, authProviderType, MemberRole.USER);
            member.setRefreshToken(newToken.getRefreshToken());
            return newToken;
        } else {
            throw new RuntimeException("토큰 재발급에 실패했습니다.ㅠㅠ");
        }
    }

    @Transactional
    public Token reissueToken(String refreshToken, JwtCode status) {
        UidDto uidDto = getUid(refreshToken);
        String email = uidDto.getEmail();
        AuthProviderType authProviderType = uidDto.getAuthProviderType();
        Member member = memberService.findByEmailAndAuthProviderType(email, authProviderType).get(); // 추후 예외처리

        if (status.equals(JwtCode.ACCESS)) {
            if (!member.equals(email, authProviderType))
                throw new RuntimeException("요청한 refreshToken이 멤버 정보와 일치하지 않습니다.");
            String accessToken = this.generateAccessToken(email, authProviderType, MemberRole.USER);
            return new Token(accessToken, refreshToken);
        } else if (status.equals(JwtCode.EXPIRED)) {
            // 멤버 정보를 비교해서 해당하는 멤버에 대해 새롭게 발급하기
            if (!member.equals(email, authProviderType))
                throw new RuntimeException("요청한 refreshToken이 멤버 정보와 일치하지 않습니다.");
            Token newToken = this.generateToken(email, authProviderType, MemberRole.USER);
            member.setRefreshToken(newToken.getRefreshToken());
            return newToken;
        } else {
            throw new RuntimeException("토큰 재발급에 실패했습니다.");
        }
    }

}
