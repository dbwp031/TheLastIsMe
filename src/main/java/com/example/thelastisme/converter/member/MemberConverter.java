package com.example.thelastisme.converter.member;

import com.example.thelastisme.auth.dto.MemberDto;
import com.example.thelastisme.auth.dto.OAuthAttributes;
import com.example.thelastisme.repository.MemberRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@RequiredArgsConstructor
@Component
public class MemberConverter {
    private final MemberRepository memberRepository;
    private static MemberRepository staticMemberRepo;
    @PostConstruct
    public void init() {
        this.staticMemberRepo = memberRepository;
    }

    public static MemberDto toDto(OAuth2User oAuth2User) {
        var attributes = oAuth2User.getAttributes();
        String registrationId = oAuth2User.getAttribute("registrationId");
        String userNameAttributeName = oAuth2User.getAttribute("userNameAttributeName");
        Map<String, Object> cloned = new HashMap<>(attributes);
        cloned.put("response", attributes);
        OAuthAttributes oAuthAttributes = OAuthAttributes.of(registrationId, userNameAttributeName, cloned);
        return MemberDto.builder()
                .email(oAuthAttributes.getEmail())
                .name(oAuthAttributes.getName())
                .authProviderType(oAuthAttributes.toEntity().getAuthProviderType())
                .build();
    }

}
