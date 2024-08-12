package com.beancontainer.domain.chatroom.controller;

import com.beancontainer.domain.chatroom.dto.ChatRoomDto;
import com.beancontainer.domain.chatroom.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor

public class ChatRoomController {
    private final ChatRoomService chatRoomService;


    @GetMapping("/rooms")
    public String getRoomsPage() {
        return "chatroomlist";  // 채팅방 리스트 페이지
    }

    @GetMapping("/room/enter/{roomId}")
    public String getChatRoomPage(@PathVariable(name = "roomId") String roomId, Model model) {
        model.addAttribute("roomId", roomId);
        return "chat";  // 특정 채팅방 입장 후의 채팅 페이지
    }


    @PostMapping("/room")
    public ChatRoomDto createRoom(@RequestParam String name) {
        return chatRoomService.createChatRoom(name);
    }
}
