package com.beancontainer.domain.chatroom.service;


import com.beancontainer.domain.chatroom.entity.UserEntity;
import com.beancontainer.domain.chatroom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserEntity registerNewUser(UserEntity user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public boolean existsByName(String name) {
        return userRepository.existsByName(name);
    }

    @Transactional(readOnly = true)
    public Optional<UserEntity> findByName(String name) {
        return userRepository.findByName(name);
    }

    @Transactional(readOnly = true)
    public Optional<UserEntity> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}