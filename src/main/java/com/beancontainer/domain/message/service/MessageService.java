package com.beancontainer.domain.message.service;


import com.beancontainer.domain.message.entity.Message;
import com.beancontainer.domain.message.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public List<Message> getMessages(Long chatRoomId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Message> messagePage = messageRepository.findByChatRoomIdOrderBySentAtDesc(chatRoomId, pageRequest);
        return messagePage.getContent();
    }

    public Message saveMessage(Message message) {
        return messageRepository.save(message);
    }
}
