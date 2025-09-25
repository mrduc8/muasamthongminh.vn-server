package mstm.muasamthongminh.muasamthongminh.modules.chat.service;

import lombok.RequiredArgsConstructor;
import mstm.muasamthongminh.muasamthongminh.modules.auth.model.User;
import mstm.muasamthongminh.muasamthongminh.modules.chat.dto.ChatMessageDto;
import mstm.muasamthongminh.muasamthongminh.modules.chat.enums.SenderType;
import mstm.muasamthongminh.muasamthongminh.modules.chat.model.Conversation;
import mstm.muasamthongminh.muasamthongminh.modules.chat.model.Message;
import mstm.muasamthongminh.muasamthongminh.modules.chat.repository.ConversationRepository;
import mstm.muasamthongminh.muasamthongminh.modules.chat.repository.MessageRepository;
import mstm.muasamthongminh.muasamthongminh.modules.shop.model.Shop;
import mstm.muasamthongminh.muasamthongminh.modules.shop.repository.ShopRepository;
import mstm.muasamthongminh.muasamthongminh.modules.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ConversationRepository conversationRepo;
    private final MessageRepository messageRepo;
    private final UserRepository userRepo;
    private final ShopRepository shopRepo;

    @Transactional
    public Conversation getOrCreateConversation(Long userId, Long shopId) {
        return conversationRepo.findByUser_IdAndShop_Id(userId, shopId)
                .orElseGet(() -> {
                    User user = userRepo.findById(userId)
                            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
                    Shop shop = shopRepo.findById(shopId)
                            .orElseThrow(() -> new IllegalArgumentException("Shop not found: " + shopId));

                    Conversation conv = Conversation.builder()
                            .user(user)
                            .shop(shop)
                            .createdAt(LocalDateTime.now()) // fix thiếu createdAt
                            .build();

                    return conversationRepo.save(conv);
                });
    }

    public Message saveMessage(ChatMessageDto dto) {
        Conversation conv = conversationRepo.findById(dto.getConversationId())
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found: " + dto.getConversationId()));

        Message msg = Message.builder()
                .conversation(conv)
                .senderType(SenderType.valueOf(dto.getSenderType()))
                .senderId(dto.getSenderId())
                .content(dto.getContent())
                .isRead(false)
                .createdAt(LocalDateTime.now()) // fix thiếu createdAt
                .build();

        return messageRepo.save(msg);
    }

    public List<Message> getMessages(Long conversationId) {
        return messageRepo.findByConversation_IdOrderByCreatedAtAsc(conversationId);
    }

    public long countUnreadMessages(Long conversationId) {
        return messageRepo.countByConversation_IdAndIsReadFalse(conversationId);
    }

    /** Đánh dấu tất cả tin nhắn trong hội thoại là đã đọc */
    @Transactional
    public void markConversationAsRead(Long conversationId) {
        messageRepo.markMessagesAsRead(conversationId);
    }

    public long countUnreadMessages(Long conversationId, SenderType senderType) {
        return messageRepo.countByConversation_IdAndSenderTypeAndIsReadFalse(conversationId, senderType);
    }
}
