package com.beancontainer.domain.chatroom.repository;

import com.beancontainer.domain.chatroom.entity.ChatListEntity;
import com.beancontainer.domain.chatroom.entity.ChatroomEntity;
import com.beancontainer.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatListRepository extends JpaRepository<ChatListEntity, Long> {
    List<ChatListEntity> findByMember(Member user);
    List<ChatListEntity> findByMemberOrderByIdDesc(Member user);
}