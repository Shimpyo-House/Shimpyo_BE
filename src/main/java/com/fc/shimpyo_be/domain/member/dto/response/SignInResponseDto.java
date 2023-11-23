package com.fc.shimpyo_be.domain.member.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignInResponseDto {

    private MemberResponseDto member;
    private TokenResponseDto token;

    @Builder
    public SignInResponseDto(MemberResponseDto member, TokenResponseDto token) {
        this.member = member;
        this.token = token;
    }

    public static SignInResponseDto of(MemberResponseDto member, TokenResponseDto token) {
        return SignInResponseDto.builder()
            .member(member)
            .token(token)
            .build();
    }
}
