package com.example.thelastisme.service;

import com.example.thelastisme.domain.UserAccount;
import com.example.thelastisme.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
@RequiredArgsConstructor
@Service
public class UserAccountService {
    private final UserAccountRepository userAccountRepository;

    public UserAccount getLastUser() {
        return userAccountRepository.findFirstByOrderByCreatedAtDesc();
    }
}
