package mstm.muasamthongminh.muasamthongminh.modules.chat.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDto {
    private Long conversationId;
    private String senderType;
    private Long senderId;
    private String content;
    private LocalDateTime createdAt;
}
