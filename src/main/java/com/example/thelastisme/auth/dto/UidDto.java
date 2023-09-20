package com.example.thelastisme.auth.dto;

import com.example.thelastisme.auth.enums.AuthProviderType;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UidDto {
    private String email;
    private AuthProviderType authProviderType;
}
