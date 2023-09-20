package com.example.thelastisme.auth.dto;

import com.example.thelastisme.auth.enums.AuthProviderType;
import com.example.thelastisme.auth.enums.MemberRole;
import com.example.thelastisme.domain.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
/*
* 리소스 서버로부터 받은 attributes를 받아오는 객체
* */
@Slf4j
@Builder
@Getter
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;
    private AuthProviderType authProviderType;

    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        log.info("OAuthAttributes - registrationId: {}, userNameAttributeName: {}", registrationId, userNameAttributeName);
        if ("kakao".equals(registrationId)) return ofKakao("id", attributes);
        else if ("google".equals(registrationId)) return ofGoogle(userNameAttributeName, attributes);
        return ofNaver("response", attributes);
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .authProviderType(AuthProviderType.GOOGLE)
                .build();
    }
    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) response.get("profile");
        return OAuthAttributes.builder()
                .name((String) profile.get("nickname"))
                .email((String) response.get("email"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .authProviderType(AuthProviderType.KAKAO)
                .build();
    }
    private static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        return OAuthAttributes.builder()
                .name((String) response.get("name"))
                .email((String) response.get("email"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .authProviderType(AuthProviderType.NAVER)
                .build();
    }

    public Member toEntity() {
        Member newMember =  Member.builder()
                .name(name)
                .email(email)
                .authProviderType(authProviderType)
                .build();
        return newMember;
    }
}
