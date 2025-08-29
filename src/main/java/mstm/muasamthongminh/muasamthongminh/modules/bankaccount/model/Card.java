package mstm.muasamthongminh.muasamthongminh.modules.bankaccount.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import mstm.muasamthongminh.muasamthongminh.modules.auth.model.User;
import mstm.muasamthongminh.muasamthongminh.modules.bankaccount.enums.CardStatus;
import mstm.muasamthongminh.muasamthongminh.modules.bankaccount.enums.CardTypes;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_cards")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @JoinColumn(name = "card_brand")
    private String cardBrand;

    @Enumerated(EnumType.STRING)
    @JoinColumn(name = "card_type")
    private CardTypes cardTypes;

    @JoinColumn(name = "card_number")
    private String cardNumber;

    @JoinColumn(name = "expiry_month")
    private int expiryMonth;

    @JoinColumn(name = "expiry_year")
    private int expiryYear;

    @JoinColumn(name = "card_holder_name")
    private String cardHolderName;

    @JoinColumn(name = "is_default")
    private boolean isDefault;

    @JoinColumn(name = "status")
    private CardStatus status;

    @JoinColumn(name = "created_at")
    private Timestamp createdAt;

    @JoinColumn(name = "updated_at")
    private Timestamp updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Timestamp(System.currentTimeMillis());
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
    }
}
