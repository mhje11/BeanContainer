package com.beancontainer.domain.chatroom.repository;

import com.beancontainer.domain.chatroom.dto.ChatRoomDto;
import com.beancontainer.domain.chatroom.entity.QChatRoom;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.beancontainer.domain.chatroom.entity.QChatRoom.chatRoom;

@Repository
public class CustomChatRoomRepositoryImpl implements CustomChatRoomRepository{

    private final EntityManager em;
    private JPAQueryFactory queryFactory;

    public CustomChatRoomRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<ChatRoomDto> findAllActiveRooms() {

        return queryFactory.select(Projections.constructor(ChatRoomDto.class,
                chatRoom.roomId,
                chatRoom.name,
                chatRoom.creator.nickname,
                chatRoom.capacity,
                chatRoom.currentUserCount))
                .from(chatRoom)
                .where(chatRoom.active.isTrue())
                .fetch();
    }
}
