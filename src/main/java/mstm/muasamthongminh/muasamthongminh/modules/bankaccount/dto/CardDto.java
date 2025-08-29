package mstm.muasamthongminh.muasamthongminh.modules.bankaccount.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import lombok.Data;
import mstm.muasamthongminh.muasamthongminh.modules.bankaccount.enums.CardStatus;
import mstm.muasamthongminh.muasamthongminh.modules.bankaccount.enums.CardTypes;

import java.sql.Timestamp;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CardDto {
    private Long id;
    private Long userId;

    private CardTypes cardTypes;
    private String cardNumber;
    private int expiryMonth;
    private int expiryYear;
    private String cardHolderName;
    private boolean isDefault;
    private CardStatus status;

    private Timestamp createdAt;
    private Timestamp updatedAt;
}
