package com.beancontainer.domain.chatroom.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class UserDto {
    private Long id;
    private String username;
    private String password;
    private String name;
    private String email;
    private LocalDateTime registrationDate;
    private Set<RoleDto> roles;
}