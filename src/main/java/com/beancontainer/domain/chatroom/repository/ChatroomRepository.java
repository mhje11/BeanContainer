package com.beancontainer.domain.chatroom.repository;

import com.beancontainer.domain.chatroom.entity.ChatroomEntity;
import com.beancontainer.domain.chatroom.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatroomRepository extends JpaRepository<ChatroomEntity, Long> {
    Optional<ChatroomEntity> findByUsersContainingAndUsersContaining(UserEntity user1, UserEntity user2);
}