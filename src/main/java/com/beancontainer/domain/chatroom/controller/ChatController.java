package com.beancontainer.domain.chatroom.controller;

import com.beancontainer.domain.chatroom.dto.ChatRoomDto;
import com.beancontainer.domain.chatroom.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/chat/chatList")
    public String chatList(Model model) {
        List<ChatRoomDto> roomList = chatService.findAllRoom();
        model.addAttribute("roomList", roomList);
        return "chat/chatList";
    }

    @PostMapping("/chat/createRoom")
    public String createRoom(Model model, @RequestParam String name) {
        ChatRoomDto room = chatService.createRoom(name);
        model.addAttribute("room", room);
        return "chat/chatRoom";
    }

    @GetMapping("/chat/chatRoom")
    public String chatRoom(Model model, @RequestParam Long roomId) {
        ChatRoomDto room = chatService.findRoomById(roomId);
        model.addAttribute("room", room);
        return "chat/chatRoom";
    }
}