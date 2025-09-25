package mstm.muasamthongminh.muasamthongminh.modules.chat.repository;

import mstm.muasamthongminh.muasamthongminh.modules.chat.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    Optional<Conversation> findByUser_IdAndShop_Id(Long userId, Long shopId);

    List<Conversation> findByShop_Id(Long shopId);

    List<Conversation> findByUser_Id(Long userId);
}
