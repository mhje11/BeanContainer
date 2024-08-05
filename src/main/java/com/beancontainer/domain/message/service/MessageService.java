package com.beancontainer.domain.message.service;


import com.beancontainer.domain.message.entity.Message;
import com.beancontainer.domain.message.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;

    @Transactional(readOnly = true)
    public List<Message> getMessages(Long chatRoomId, int page, int size) {
        // Implement pagination logic here
        return messageRepository.findByChatRoomIdOrderBySentAtDesc(chatRoomId);
    }

    @Transactional
    public void sendMessage(Message message) {
        messageRepository.save(message);
    }
}
