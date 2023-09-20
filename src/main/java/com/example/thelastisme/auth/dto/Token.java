package com.example.thelastisme.auth.dto;

import lombok.*;

@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Token {
    private String accessToken;
    private String refreshToken;
}
