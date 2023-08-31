package com.example.thelastisme.repository;

import com.example.thelastisme.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    UserAccount findFirstByOrderByCreatedAtDesc();
    List<UserAccount> findAllByOrderByCreatedAtDesc();
}
