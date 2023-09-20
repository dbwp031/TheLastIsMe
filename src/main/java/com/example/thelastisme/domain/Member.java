package com.example.thelastisme.domain;

import com.example.thelastisme.auth.enums.AuthProviderType;
import com.example.thelastisme.auth.enums.MemberRole;
import com.example.thelastisme.repository.RoleRepository;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Builder.Default
    private List<Role> roles = new ArrayList<>();
    private String refreshToken;

    @Enumerated(EnumType.STRING)
    private AuthProviderType authProviderType;

    private String name;
    private String email;

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    public void setRoles(List<MemberRole> roleEnums, RoleRepository roleRepository) {
        List<Role> newRoles = roleEnums.stream().map((roleEnum) -> Role.builder()
                .memberRole(roleEnum).member(this).build()).collect(Collectors.toList());
        this.roles = newRoles;
    }

    public Member update(String name) {
        this.name = name;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return email.equals(member.email) && authProviderType == member.authProviderType;
    }

    public boolean equals(String email, AuthProviderType providerType) {
        if (this.getEmail().equals(email) && this.getAuthProviderType().equals(providerType)) return true;
        else return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, authProviderType);
    }
}
