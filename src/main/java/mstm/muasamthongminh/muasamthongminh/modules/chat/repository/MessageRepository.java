package mstm.muasamthongminh.muasamthongminh.modules.chat.repository;

import mstm.muasamthongminh.muasamthongminh.modules.chat.enums.SenderType;
import mstm.muasamthongminh.muasamthongminh.modules.chat.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByConversation_IdOrderByCreatedAtAsc(Long conversationId);

    Optional<Message> findTopByConversation_IdOrderByCreatedAtDesc(Long conversationId);

    long countByConversation_IdAndIsReadFalse(Long conversationId);

    long countByConversation_IdAndSenderTypeAndIsReadFalse(Long conversationId, SenderType senderType);

    // Đánh dấu tất cả tin nhắn là đã đọc
    @Modifying
    @Query("UPDATE Message m SET m.isRead = true WHERE m.conversation.id = :conversationId")
    void markMessagesAsRead(@Param("conversationId") Long conversationId);
}
