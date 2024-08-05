package com.beancontainer.domain.message.controller;


import com.beancontainer.domain.message.entity.Message;
import com.beancontainer.domain.message.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MessageController {

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/chat/{chatRoomId}/messages")
    public List<Message> getMessages(@PathVariable Long chatRoomId, @RequestParam int page, @RequestParam int size) {
        return messageService.getMessages(chatRoomId, page, size);
    }

    @PostMapping("/chat/sendMessage")
    public Message sendMessage(@RequestBody Message message) {
        return messageService.saveMessage(message);
    }
}
