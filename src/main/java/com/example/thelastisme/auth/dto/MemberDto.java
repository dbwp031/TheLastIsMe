package com.example.thelastisme.auth.dto;

import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class MemberDto {
    private Long id;
    private String email;
    private String name;
}