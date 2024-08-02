package com.beancontainer.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class LoginDto {
    private String userId; //로그인Id
    private String password;
}
