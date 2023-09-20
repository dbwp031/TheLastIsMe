package com.example.thelastisme.auth.repository;

import com.example.thelastisme.auth.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    public RefreshToken findByMemberId(Long memberId);
}
