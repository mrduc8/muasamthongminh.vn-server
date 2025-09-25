package mstm.muasamthongminh.muasamthongminh.modules.chat.controller;

import lombok.RequiredArgsConstructor;
import mstm.muasamthongminh.muasamthongminh.modules.chat.dto.ChatMessageDto;
import mstm.muasamthongminh.muasamthongminh.modules.chat.model.Message;
import mstm.muasamthongminh.muasamthongminh.modules.chat.service.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class ChatWsController {
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(ChatMessageDto dto) {


        // Lưu DB
        Message saved = chatService.saveMessage(dto);

        // Broadcast lại cho FE
        ChatMessageDto response = ChatMessageDto.builder()
                .conversationId(saved.getConversation().getId())
                .senderType(saved.getSenderType().name())
                .senderId(saved.getSenderId())
                .content(saved.getContent())
                .createdAt(saved.getCreatedAt() != null ? saved.getCreatedAt() : LocalDateTime.now())
                .build();

        messagingTemplate.convertAndSend("/topic/conversations/" + saved.getConversation().getId(), response);
    }
}
