package mstm.muasamthongminh.muasamthongminh.modules.bankaccount.mapper;

import mstm.muasamthongminh.muasamthongminh.modules.auth.model.User;
import mstm.muasamthongminh.muasamthongminh.modules.bankaccount.dto.CardDto;
import mstm.muasamthongminh.muasamthongminh.modules.bankaccount.enums.CardStatus;
import mstm.muasamthongminh.muasamthongminh.modules.bankaccount.model.Card;

public class CardMapper {
    public static Card toEntity(CardDto dto, User user) {
        if (dto == null) return null;
        if (user == null) return null;

       return Card.builder()
               .id(dto.getId())
               .user(user)
               .cardTypes(dto.getCardTypes())
               .cardNumber(String.valueOf(dto.getCardNumber()))
               .expiryMonth(dto.getExpiryMonth())
               .expiryYear(dto.getExpiryYear())
               .cardHolderName(dto.getCardHolderName())
               .isDefault(dto.isDefault())
               .status(CardStatus.ACTIVE)
               .createdAt(dto.getCreatedAt())
               .updatedAt(dto.getUpdatedAt())
               .build();
    }

    public static CardDto toDto(Card card) {
        if (card == null) return null;

        CardDto dto = new CardDto();
        dto.setId(card.getId());
        dto.setUserId(card.getUser() != null ? card.getUser().getId() : null);
        dto.setCardTypes(card.getCardTypes());
        dto.setCardNumber(card.getCardNumber());
        dto.setExpiryMonth(card.getExpiryMonth());
        dto.setExpiryYear(card.getExpiryYear());
        dto.setCardHolderName(card.getCardHolderName());
        dto.setDefault(card.isDefault());
        dto.setStatus(CardStatus.ACTIVE);
        dto.setCreatedAt(card.getCreatedAt());
        dto.setUpdatedAt(card.getUpdatedAt());
        return dto;
    }
}
