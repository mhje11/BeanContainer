package com.beancontainer.domain.chatroom.controller;

import com.beancontainer.domain.chatroom.dto.ChatRoomDto;
import com.beancontainer.domain.chatroom.service.ChatRoomService;
import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.repository.MemberRepository;
import com.beancontainer.global.exception.CustomException;
import com.beancontainer.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Controller
@RequestMapping("/chat")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final MemberRepository memberRepository;

    @GetMapping("/room")
    public String rooms(Model model) {
        return "/chat/room";
    }

    @GetMapping("/rooms")
    @ResponseBody
    public List<ChatRoomDto> room() {
        return chatRoomService.findAllRoom();
    }

    @PostMapping("/room")
    @ResponseBody
    public ChatRoomDto createRoom(@RequestParam String name) {
        return chatRoomService.createChatRoom(name);
    }

    @GetMapping("/room/enter/{roomId}")
    public String roomDetail(Model model, @PathVariable Long roomId, @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("roomId", roomId);
        Member userId = memberRepository.findByUserId(userDetails.getUsername())
                .orElseThrow(() -> new CustomException(ExceptionCode.MEMBER_NOT_FOUND));
        model.addAttribute("userId", userId.getNickname());

        return "/chat/roomdetail";
    }

    @GetMapping("/room/{roomId}")
    @ResponseBody
    public ChatRoomDto roomInfo(@PathVariable Long roomId) {
        return chatRoomService.findRoomById(roomId);
    }
}