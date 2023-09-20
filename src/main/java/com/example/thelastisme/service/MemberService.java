package com.example.thelastisme.service;

import com.example.thelastisme.auth.enums.AuthProviderType;
import com.example.thelastisme.domain.Member;
import com.example.thelastisme.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;

    public Optional<Member> findByEmailAndAuthProviderType(String email, AuthProviderType authProviderType) {
        return memberRepository.findByEmailAndAuthProviderType(email, authProviderType);
    }
}
