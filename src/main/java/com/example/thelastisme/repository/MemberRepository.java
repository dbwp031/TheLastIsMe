package com.example.thelastisme.repository;

import com.example.thelastisme.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
