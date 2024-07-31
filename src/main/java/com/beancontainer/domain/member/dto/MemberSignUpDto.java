package com.beancontainer.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//회원가입에 대한 DTO
@Getter @Setter
@NoArgsConstructor
public class MemberSignUpDto {
    @NotBlank(message = "아이디를 입력하세요.") //공백 불가능
    private String userId;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 8, max = 20, message = "비밀번호는 8~20자.") //비밀번호 길이 설정
    private String password;


    private String name;
    private String role;


}
