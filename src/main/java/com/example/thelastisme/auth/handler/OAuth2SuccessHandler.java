package com.example.thelastisme.auth.handler;

import com.example.thelastisme.auth.dto.MemberDto;
import com.example.thelastisme.auth.dto.Token;
import com.example.thelastisme.auth.enums.MemberRole;
import com.example.thelastisme.auth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.example.thelastisme.auth.service.TokenService;
import com.example.thelastisme.config.properties.SecurityProperties;
import com.example.thelastisme.converter.member.MemberConverter;
import com.example.thelastisme.repository.MemberRepository;
import com.example.thelastisme.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Optional;
import static com.example.thelastisme.auth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@Slf4j
@AllArgsConstructor
public class OAuth2SuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private TokenService tokenService;
    private MemberRepository memberRepository;
    private OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
    private SecurityProperties securityProperties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        MemberDto memberDto = MemberConverter.toDto(oAuth2User);
        Token token = tokenService.generateToken(memberDto.getEmail(), memberDto.getAuthProviderType(), MemberRole.USER);

        memberRepository.findByEmailAndAuthProviderType(memberDto.getEmail(), memberDto.getAuthProviderType())
                .ifPresent((member -> {
                    member.setRefreshToken(token.getRefreshToken());
                    memberRepository.save(member);
                }));
        log.info("OAuth2SuccessHandler Token: {}", token);

        writeTokenResponse(response, token);
        String targetUrl = this.determineTargetUrlDelegate(request, response);
        this.clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    protected String determineTargetUrlDelegate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        return this.determineTargetUrl(request, response);
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
        StringBuilder stringBuilder = new StringBuilder();
        String defaultUrl = stringBuilder.append(securityProperties.getScheme()).append("://")
                .append(securityProperties.getDefaultHost()).append(":")
                .append(securityProperties.getPort())
                .append(securityProperties.getDefaultSuccessPath()).toString();
        System.out.println("defaultUrl: " + defaultUrl);
        Optional<String> redirectUrl = CookieUtil.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue)
                .map((path) -> stringBuilder.append(securityProperties.getScheme()).append("://")
                        .append(securityProperties.getDefaultHost()).append(":")
                        .append(securityProperties.getPort())
                        .append(path).toString());
        System.out.println("redirectUrl: "+ redirectUrl);

        String targetUrl = redirectUrl.orElse(defaultUrl);
        return targetUrl;
    }
    private void writeTokenResponse(HttpServletResponse response, Token token) {
        response.setContentType("text/html;charset=UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        ResponseCookie accessTokenCookie = CookieUtil.createAccessTokenCookie(token.getAccessToken());
        ResponseCookie refreshTokenCookie = CookieUtil.createRefreshTokenCookie(token.getRefreshToken());
        response.addHeader("Set-Cookie", accessTokenCookie.toString());
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());
    }
}
