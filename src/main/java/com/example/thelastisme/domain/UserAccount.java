package com.example.thelastisme.domain;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
@Getter
@EntityListeners(AuditingEntityListener.class)
@Entity
public class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(nullable = false)
    String name;

    @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME)
    @CreatedDate
    @Column(nullable = false, updatable = false)
    LocalDateTime createdAt;
    private UserAccount() {
    }
    private UserAccount(String name) {
        this.name = name;
        this.createdAt = LocalDateTime.now();
    }
    public static UserAccount of(String name) {
        return new UserAccount(name);
    }
}
