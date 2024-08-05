package com.beancontainer.domain.message.controller;



import com.beancontainer.domain.message.entity.Message;
import com.beancontainer.domain.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @GetMapping("/chatroom/{roomId}")
    public List<Message> getMessages(@PathVariable Long roomId, @RequestParam int page, @RequestParam int size) {
        return messageService.getMessages(roomId, page, size);
    }
}
