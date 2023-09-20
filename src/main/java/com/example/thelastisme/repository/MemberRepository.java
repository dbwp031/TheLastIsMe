package com.example.thelastisme.repository;

import com.example.thelastisme.auth.enums.AuthProviderType;
import com.example.thelastisme.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);

    Optional<Member> findByEmailAndAuthProviderType(String email, AuthProviderType authProviderType);
}
