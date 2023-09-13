package com.example.thelastisme.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientPropertiesMapper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;

import java.util.List;
/*
* ConditionalOnMissingBean: 지정된 타입의 빈이 이미 Spring Context에 존재하지 않을 경우에만 현재 빈을 생성하도록 한다.
* */
@Configuration
@EnableConfigurationProperties(OAuth2ClientProperties.class)
public class ClientRepositoryConfig {
    @Bean("clientRepository")
    @ConditionalOnMissingBean(ClientRegistrationRepository.class)
    InMemoryClientRegistrationRepository clientRegistrationRepository(OAuth2ClientProperties oAuth2ClientProperties) {
        OAuth2ClientPropertiesMapper mapper = new OAuth2ClientPropertiesMapper(oAuth2ClientProperties);
        List<ClientRegistration> registrations = mapper.asClientRegistrations().values().stream().toList();
        return new InMemoryClientRegistrationRepository(registrations);
    }
}
