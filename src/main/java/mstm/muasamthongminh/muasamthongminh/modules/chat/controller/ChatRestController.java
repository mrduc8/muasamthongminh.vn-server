package mstm.muasamthongminh.muasamthongminh.modules.chat.controller;

import lombok.RequiredArgsConstructor;
import mstm.muasamthongminh.muasamthongminh.modules.auth.model.User;
import mstm.muasamthongminh.muasamthongminh.modules.auth.security.CustomUserDetails;
import mstm.muasamthongminh.muasamthongminh.modules.chat.dto.ChatMessageDto;
import mstm.muasamthongminh.muasamthongminh.modules.chat.enums.SenderType;
import mstm.muasamthongminh.muasamthongminh.modules.chat.model.Conversation;
import mstm.muasamthongminh.muasamthongminh.modules.chat.model.Message;
import mstm.muasamthongminh.muasamthongminh.modules.chat.repository.ConversationRepository;
import mstm.muasamthongminh.muasamthongminh.modules.chat.repository.MessageRepository;
import mstm.muasamthongminh.muasamthongminh.modules.chat.service.ChatService;
import mstm.muasamthongminh.muasamthongminh.modules.shop.service.ShopService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRestController {
    private final ChatService chatService;
    private final MessageRepository messageRepo;
    private final ConversationRepository conversationRepo;
    private final ShopService shopService;

    @GetMapping("/messages/{conversationId}")
    public ResponseEntity<List<ChatMessageDto>> getMessages(@PathVariable Long conversationId) {
        var msgs = chatService.getMessages(conversationId).stream()
                .map(m -> ChatMessageDto.builder()
                        .conversationId(m.getConversation().getId())
                        .senderType(m.getSenderType().name())
                        .senderId(m.getSenderId())
                        .content(m.getContent())
                        .createdAt(m.getCreatedAt())
                        .build())
                .toList();
        return ResponseEntity.ok(msgs);
    }

    @PostMapping("/conversation")
    public ResponseEntity<?> getOrCreateConversation(
            @RequestParam Long shopId,
            Authentication authentication
    ) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("Authentication is null");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof CustomUserDetails userDetails)) {
            return ResponseEntity.status(401).body("Principal is not CustomUserDetails: " + principal);
        }

        Long userId = userDetails.getId();
        var conversation = chatService.getOrCreateConversation(userId, shopId);

        return ResponseEntity.ok(Map.of(
                "id", conversation.getId(),
                "userId", conversation.getUser().getId(),
                "shopId", conversation.getShop().getId()
        ));
    }


    @GetMapping("/my-conversations")
    public ResponseEntity<?> getConversationsByUser(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("Authentication is null");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof CustomUserDetails userDetails)) {
            return ResponseEntity.status(401).body("Principal is not CustomUserDetails: " + principal);
        }

        Long userId = userDetails.getId();

        // Tìm tất cả conversation theo userId
        List<Conversation> conversations = conversationRepo.findByUser_Id(userId);

        if (conversations.isEmpty()) {
            return ResponseEntity.ok(List.of()); // không có thì trả list rỗng
        }

        List<Map<String, Object>> result = conversations.stream().map(c -> {
            Message lastMsg = messageRepo.findTopByConversation_IdOrderByCreatedAtDesc(c.getId())
                    .orElse(null);

            // Unread = đếm tin nhắn từ shop mà user chưa đọc
            long unreadCount = chatService.countUnreadMessages(c.getId(), SenderType.SHOP);

            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getId());
            map.put("shopName", c.getShop().getShopName());
            map.put("shopLogo", c.getShop().getLogoUrl());
            map.put("lastMessage", lastMsg != null ? lastMsg.getContent() : "");
            map.put("unreadCount", unreadCount);
            return map;
        }).toList();

        return ResponseEntity.ok(result);
    }


    @GetMapping("/conversations")
    public ResponseEntity<?> getConversationsByShop(
            @RequestParam Long shopId,
            Authentication authentication
    ) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("Authentication is null");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof CustomUserDetails userDetails)) {
            return ResponseEntity.status(401).body("Principal is not CustomUserDetails: " + principal);
        }

        Long userId = userDetails.getId();

        var shop = shopService.getShopByUserId(userId);

        if (!shop.getId().equals(shopId)) {
            return ResponseEntity.status(403).body("Bạn không có quyền xem hội thoại của shop này");
        }

        List<Conversation> conversations = conversationRepo.findByShop_Id(shopId);

        if (conversations.isEmpty()) {
            return ResponseEntity.ok(List.of()); // không có thì trả list rỗng
        }

        List<Map<String, Object>> result = conversations.stream().map(c -> {
            Message lastMsg = messageRepo.findTopByConversation_IdOrderByCreatedAtDesc(c.getId())
                    .orElse(null);

            long unreadCount = chatService.countUnreadMessages(c.getId(), SenderType.USER);

            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getId());
            map.put("userName", c.getUser().getName());
            map.put("avatarUrl", c.getUser().getAvatar());
            map.put("lastMessage", lastMsg != null ? lastMsg.getContent() : "");
            map.put("unreadCount", unreadCount);
            return map;
        }).toList();

        return ResponseEntity.ok(result);
    }

    @PostMapping("/conversations/{conversationId}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long conversationId,
                                        Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("Authentication is null");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof CustomUserDetails userDetails)) {
            return ResponseEntity.status(401).body("Principal is not CustomUserDetails: " + principal);
        }

        Long userId = userDetails.getId();
        var shop = shopService.getShopByUserId(userId);

        Conversation conv = conversationRepo.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        if (!conv.getShop().getId().equals(shop.getId())) {
            return ResponseEntity.status(403).body("Bạn không có quyền đánh dấu hội thoại này");
        }

        chatService.markConversationAsRead(conversationId);
        return ResponseEntity.ok(Map.of("message", "Marked as read"));
    }
}
