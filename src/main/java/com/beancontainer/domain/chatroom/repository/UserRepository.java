package com.beancontainer.domain.chatroom.repository;

import com.beancontainer.domain.chatroom.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByName(String name);
    boolean existsByName(String name);
}