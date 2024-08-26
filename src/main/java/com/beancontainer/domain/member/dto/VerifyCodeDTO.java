package com.beancontainer.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

//이메일 인증번호 코드
@Getter @Setter
@AllArgsConstructor
public class VerifyCodeDTO {
    private String email;
    private String code;
}
