package com.beancontainer.domain.chatroom.service;

import com.beancontainer.domain.chatroom.dto.RoleDto;
import com.beancontainer.domain.chatroom.dto.UserDto;
import com.beancontainer.domain.chatroom.entity.Role;
import com.beancontainer.domain.chatroom.entity.User;
import com.beancontainer.domain.chatroom.repository.RoleRepository;
import com.beancontainer.domain.chatroom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Transactional(readOnly = false)
    public UserDto registerNewUser(UserDto userDto) {
        Role userRole = roleRepository.findByName("USER");

        User user = new User(
                userDto.getUsername(),
                passwordEncoder.encode(userDto.getPassword()),
                userDto.getName(),
                userDto.getEmail(),
                Collections.singleton(userRole)
        );

        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }

    @Transactional(readOnly = true)
    public UserDto findByUsername(String username) {
        User user = userRepository.findByUsername(username);
        return convertToDto(user);
    }

    private UserDto convertToDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setRegistrationDate(user.getRegistrationDate());
        userDto.setRoles(user.getRoles().stream()
                .map(role -> {
                    RoleDto roleDto = new RoleDto();
                    roleDto.setId(role.getId());
                    roleDto.setName(role.getName());
                    return roleDto;
                }).collect(Collectors.toSet()));
        return userDto;
    }
}
