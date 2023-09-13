package com.example.thelastisme.domain;

import com.example.thelastisme.auth.enums.AuthProviderType;
import com.example.thelastisme.auth.enums.MemberRole;
import com.example.thelastisme.repository.RoleRepository;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
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

    private String name;
    private String email;

    public void setRoles(List<MemberRole> roleEnums, RoleRepository roleRepository) {
        List<Role> newRoles = roleEnums.stream().map((roleEnum) -> Role.builder()
                .memberRole(roleEnum).member(this).build()).collect(Collectors.toList());
        this.roles = newRoles;
    }
    public void setRoles(List<MemberRole> roleEnums) {
        List<Role> newRoles = roleEnums.stream().map((roleEnum) -> Role.builder()
                .memberRole(roleEnum).member(this).build()).collect(Collectors.toList());
        this.roles = newRoles;
    }

    public Member update(String name) {
        this.name = name;
        return this;
    }
}
