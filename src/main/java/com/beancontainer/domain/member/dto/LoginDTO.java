package com.beancontainer.domain.member.dto;

import com.beancontainer.domain.member.entity.Role;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {
    private String userId;

    //@Pattern(regexp=  "^(?=.[a-zA-Z])(?=.\d)(?=.*\W).{8,20}$")
    private String password;

    private String accessToken;
    private String refreshToken;
    private String name;
    private Role role;
}
