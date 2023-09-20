//package com.example.thelastisme.auth.provider;
//
//import com.example.thelastisme.auth.domain.RefreshToken;
//import com.example.thelastisme.auth.dto.Token;
//import com.example.thelastisme.auth.repository.RefreshTokenRepository;
//import io.jsonwebtoken.*;
//import io.jsonwebtoken.security.Keys;
//import jakarta.xml.bind.DatatypeConverter;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.security.Key;
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.Date;
//import java.util.stream.Collectors;
//@Slf4j
//@Component
//public class JwtTokenProvider implements AuthenticationProvider {
//    private final Key secret;
//    private final Date accessExpirationDate;
//    private final Date refreshExpirationDate;
//    private final RefreshTokenRepository refreshTokenRepository;
//
//    @Autowired
//    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey, RefreshTokenRepository refreshTokenRepository) {
//        this.accessExpirationDate = new Date(System.currentTimeMillis() + 1000 * 60 * 30); //30분
//        this.refreshExpirationDate = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 3); //3일
//        this.refreshTokenRepository = refreshTokenRepository;
//        byte[] secretByteKey = DatatypeConverter.parseBase64Binary(secretKey);
//        this.secret = Keys.hmacShaKeyFor(secretByteKey);
//    }
//    @Transactional
//    public Token generateToken(Authentication authentication, String memberId) {
//        return Token.builder()
//                .grantType("Bearer")
//                .accessToken(generateAccessToken(authentication, memberId))
//                .refreshToken(generateRefreshToken(memberId))
//                .build();
//    }
//
//    private String generateAccessToken(Authentication authentication, String memberId) {
//        String authorities = authentication.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.joining(","));
//        log.info("memberId: " + memberId + ", Auth: " + authorities);
//
//        return Jwts.builder()
//                .setSubject(memberId)
//                .claim("auth", authorities)
//                .setExpiration(accessExpirationDate)
//                .signWith(secret, SignatureAlgorithm.HS256)
//                .compact();
//    }
//    @Transactional
//    public String generateRefreshToken(String memberId) {
//        String refreshToken = null;
//        RefreshToken dbRefreshToken = refreshTokenRepository.findByMemberId(Long.parseLong(memberId));
//        if (dbRefreshToken == null) {
//            refreshToken = Jwts.builder()
//                    .setSubject(memberId)
//                    .setExpiration(refreshExpirationDate)
//                    .signWith(secret, SignatureAlgorithm.HS256)
//                    .compact();
//
//
//            RefreshToken refreshTokenEntity = RefreshToken.builder()
//                    .memberId(Long.parseLong(memberId))
//                    .refreshToken(refreshToken)
//                    .build();
//
//            refreshTokenRepository.save(refreshTokenEntity);
//        } else {
//            refreshToken = dbRefreshToken.getRefreshToken();
//        }
//        return refreshToken;
//    }
//
//    public boolean validateAccessToken(String accessToken) {
//        try {
//            Jwts.parserBuilder().setSigningKey(secret).build().parseClaimsJws(accessToken);
//            return true;
//        } catch (io.jsonwebtoken.security.SignatureException | MalformedJwtException e) {
//            log.info("Invalid JWT Token", e);
//        } catch (ExpiredJwtException e) {
//            log.info("Expired JWT Token", e);
//        } catch (UnsupportedJwtException e) {
//            log.info("Unsupported JWT Token", e);
//        } catch (IllegalArgumentException e) {
//            log.info("JWT claims string is empty.", e);
//        } catch (Exception e) {
//            log.info("오류가 발생했습니다.", e);
//        }
//        return false;
//    }
//
//    public Long validateRefreshToken(String refreshToken) {
//        try {
//            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(secret).build().parseClaimsJws(refreshToken);
//            Long memberId = Long.parseLong(claims.getBody().getSubject());
//            RefreshToken refreshTokenEntity = refreshTokenRepository.findByMemberId(memberId);
//            if (!refreshTokenEntity.getRefreshToken().equals(refreshToken)) throw new Exception("토큰 정보가 DB와 일치하지 않습니다");
//            return memberId;
//        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
//            log.info("Invalid JWT Token", e);
//        } catch (ExpiredJwtException e) {
//            log.info("Expired JWT Token", e);
//        } catch (UnsupportedJwtException e) {
//            log.info("Unsupported JWT Token", e);
//        } catch (IllegalArgumentException e) {
//            log.info("JWT claims string is empty.", e);
//        } catch (Exception e) {
//            log.info("올바르지 않은 JWT 토큰입니다.", e);
//        }
//        return null;
//    }
//
//    public Authentication getAuthentication(String accessToken) {
//        Claims claims = parseClaims(accessToken);
//
//        if (claims.get("auth") == null) {
//            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
//        } else if (claims.getSubject() == null) {
//            throw new RuntimeException("멤버 정보가 없는 토큰입니다.");
//        }
//        Collection<? extends GrantedAuthority> authorities = Arrays.stream(
//                claims.get("auth").toString().split(",")
//        ).map(SimpleGrantedAuthority::new)
//        .collect(Collectors.toList());
//
//        UserDetails principal = User
//    }
//
//    private Claims parseClaims(String accessToken) {
//        try {
//            return Jwts.parserBuilder().setSigningKey(secret).build().parseClaimsJws(accessToken).getBody();
//        } catch (ExpiredJwtException e) {
//            return e.getClaims();
//        }
//    }
//
//    @Override
//    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//        JwtAuthen
//    }
//
//    @Override
//    public boolean supports(Class<?> authentication) {
//        return false;
//    }
//}
