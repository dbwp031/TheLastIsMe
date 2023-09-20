package com.example.thelastisme.auth.service;

import com.example.thelastisme.auth.dto.OAuthAttributes;
import com.example.thelastisme.auth.dto.SessionUser;
import com.example.thelastisme.auth.enums.MemberRole;
import com.example.thelastisme.domain.Member;
import com.example.thelastisme.repository.MemberRepository;
import com.example.thelastisme.repository.RoleRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.security.AuthProvider;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        return processOAuth2User(userRequest, oAuth2User);
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();
        log.debug("registrationId: {}, userNameAttributeName: {}",registrationId, userNameAttributeName);

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());
        Map<String, Object> modifiableAttributes = new HashMap<>(attributes.getAttributes());
        modifiableAttributes.put("registrationId", registrationId);
        modifiableAttributes.put("userNameAttributeName", userNameAttributeName);
        Member user = saveOrUpdate(attributes);

        return new DefaultOAuth2User(
                user.getRoles(),
                modifiableAttributes,
                attributes.getNameAttributeKey()
        );
    }

    private Member newMember(OAuthAttributes attributes) {
        Member newMember = attributes.toEntity();
        newMember.setRoles(Arrays.asList(MemberRole.USER), this.roleRepository);
        return newMember;
    }
    private Member saveOrUpdate(OAuthAttributes attributes) {
        Member member = memberRepository.findByEmailAndAuthProviderType(attributes.getEmail(), attributes.getAuthProviderType())
                .map(entity->entity.update(attributes.getName()))
                .orElseGet(() -> newMember(attributes));

        return memberRepository.save(member);
    }
}
