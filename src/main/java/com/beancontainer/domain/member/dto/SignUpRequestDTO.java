package com.beancontainer.domain.member.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequestDTO {
    //회원가입 유저 ID
    private String userId;

    //회원가입 유저 Password
    private String password;

    //회원 이름
    private String name;

    //회원 닉네임
    private String nickname;

    //이메일 인증
    private String email;
}