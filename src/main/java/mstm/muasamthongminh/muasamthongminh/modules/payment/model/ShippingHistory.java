package mstm.muasamthongminh.muasamthongminh.modules.payment.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;
import mstm.muasamthongminh.muasamthongminh.modules.payment.enums.ShippingStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "shipping_history")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShippingHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_id", nullable = false)
    private Shippings shippingId;

    @Enumerated(EnumType.STRING)
    @JoinColumn(name = "status")
    private ShippingStatus status;

    @Column(name = "note")
    private String note;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
