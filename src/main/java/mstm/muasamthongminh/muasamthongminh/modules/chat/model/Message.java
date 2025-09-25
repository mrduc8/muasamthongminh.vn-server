package mstm.muasamthongminh.muasamthongminh.modules.chat.model;

import jakarta.persistence.*;
import lombok.*;
import mstm.muasamthongminh.muasamthongminh.modules.chat.enums.SenderType;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @Enumerated(EnumType.STRING)
    @Column(name = "sender_type", nullable = false)
    private SenderType senderType; // USER / SHOP

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private Boolean isRead = false;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
