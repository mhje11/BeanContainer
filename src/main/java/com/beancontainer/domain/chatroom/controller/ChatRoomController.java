package com.beancontainer.domain.chatroom.controller;


import com.beancontainer.domain.chatroom.dto.ChatRoomDto;
import com.beancontainer.domain.chatroom.service.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @Autowired
    public ChatRoomController(ChatRoomService chatRoomService) {
        this.chatRoomService = chatRoomService;
    }

    @GetMapping("/chatlist")
    public List<ChatRoomDto> getChatRooms() {
        return chatRoomService.getAllChatRooms();
    }

    @GetMapping("/mychatlist/{username}")
    public List<ChatRoomDto> getUserChatRooms(@PathVariable String username) {
        return chatRoomService.getUserChatRooms(username);
    }

    @PostMapping("/chat/create")
    public ChatRoomDto createChatRoom(@RequestBody ChatRoomDto chatRoomDto) {
        return chatRoomService.createChatRoom(chatRoomDto);
    }

    @DeleteMapping("/chat/delete/{chatId}")
    public ResponseEntity<?> deleteChatRoom(@PathVariable Long chatId) {
        chatRoomService.deleteChatRoom(chatId);
        return ResponseEntity.ok().body("방 삭제가 완료되었습니다.");
    }
}
