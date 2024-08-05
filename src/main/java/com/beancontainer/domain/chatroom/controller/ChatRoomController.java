package com.beancontainer.domain.chatroom.controller;


import com.beancontainer.domain.chatroom.dto.ChatRoomDto;
import com.beancontainer.domain.chatroom.entity.ChatRoom;
import com.beancontainer.domain.chatroom.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @GetMapping("/chatlist")
    public List<ChatRoomDto> getChatRooms() {
        return chatRoomService.getAllChatRooms();
    }

    @GetMapping("/mychatlist/{ownerName}")
    public List<ChatRoomDto> getMyChatRooms(@PathVariable String ownerName) {
        return chatRoomService.getMyChatRooms(ownerName);
    }

    @PostMapping("/create")
    public ChatRoom createChatRoom(@RequestBody ChatRoom chatRoom) {
        return chatRoomService.createChatRoom(chatRoom);
    }

    @DeleteMapping("/delete/{chatId}")
    public ResponseEntity<?> deleteChatRoom(@PathVariable Long chatId) {
        boolean isDeleted = chatRoomService.deleteChatRoom(chatId);
        if (isDeleted) {
            return ResponseEntity.ok().body("{\"message\": \"방 삭제가 완료되었습니다.\"}");
        } else {
            return ResponseEntity.badRequest().body("{\"error\": \"잘못된 요청입니다.\"}");
        }
    }
}
