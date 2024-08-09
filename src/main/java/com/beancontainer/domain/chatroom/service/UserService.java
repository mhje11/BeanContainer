package com.beancontainer.domain.chatroom.service;

import com.beancontainer.domain.chatroom.entity.Role;
import com.beancontainer.domain.chatroom.entity.User;
import com.beancontainer.domain.chatroom.repository.RoleRepository;
import com.beancontainer.domain.chatroom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserService {
    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Transactional(readOnly = false)
    public User registerNewUser(User user) {
        Role userRole = roleRepository.findByName("USER");
        user.setRoles(Collections.singleton(userRole));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}