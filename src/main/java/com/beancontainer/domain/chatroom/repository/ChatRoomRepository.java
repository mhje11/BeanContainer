package com.beancontainer.domain.chatroom.repository;

import com.beancontainer.domain.chatroom.entity.ChatRoom;
import com.beancontainer.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long>, CustomChatRoomRepository {
    List<ChatRoom> findAllByMember(Member member);
}