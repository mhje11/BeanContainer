package com.beancontainer.domain.chatroom.controller;

import com.beancontainer.domain.chatroom.model.ChatRoom;
import com.beancontainer.domain.chatroom.service.ChatRoomService;
import com.beancontainer.global.jwt.util.JwtTokenizer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/chat")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final JwtTokenizer jwtTokenProvider;

    @GetMapping("/room")
    public String rooms() {
        return "chat/room";
    }

    @GetMapping("/rooms")
    @ResponseBody
    public ResponseEntity<List<ChatRoom>> room() {
        List<ChatRoom> chatRooms = chatRoomService.findAllRoom();
        chatRooms.forEach(room -> room.updateUserCount(chatRoomService.getUserCount(room.getRoomId())));
        return ResponseEntity.ok(chatRooms);
    }

    @PostMapping("/room")
    @ResponseBody
    public ResponseEntity<ChatRoom> createRoom(@RequestParam String name) {
        ChatRoom chatRoom = chatRoomService.createChatRoom(name);
        return ResponseEntity.ok(chatRoom);
    }

    @GetMapping("/room/enter/{roomId}")
    public String roomDetail(Model model, @PathVariable String roomId) {
        model.addAttribute("roomId", roomId);
        return "chat/roomdetail";
    }

    @GetMapping("/room/{roomId}")
    @ResponseBody
    public ResponseEntity<ChatRoom> roomInfo(@PathVariable String roomId) {
        ChatRoom chatRoom = chatRoomService.findRoomById(roomId);
        return ResponseEntity.ok(chatRoom);
    }
}
