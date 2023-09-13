package com.example.thelastisme.auth.dto;

import com.example.thelastisme.auth.enums.AuthProviderType;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class MemberDto {
    private Long id;
    private String email;
    private String name;
    private AuthProviderType authProviderType;
}