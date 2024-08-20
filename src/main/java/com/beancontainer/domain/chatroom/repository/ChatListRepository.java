package com.beancontainer.domain.chatroom.repository;

import com.beancontainer.domain.chatroom.entity.ChatListEntity;
import com.beancontainer.domain.chatroom.entity.ChatroomEntity;
import com.beancontainer.domain.chatroom.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatListRepository extends JpaRepository<ChatListEntity, Long> {
    List<ChatListEntity> findByUser(UserEntity user);
    boolean existsByUserAndChatroom(UserEntity user, ChatroomEntity chatroom);
    //    List<ChatListEntity> findByUserOrderByUpdatedAtDesc(UserEntity user);
    List<ChatListEntity> findByUserOrderByIdDesc(UserEntity user);
}