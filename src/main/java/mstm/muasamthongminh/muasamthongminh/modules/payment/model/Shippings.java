package mstm.muasamthongminh.muasamthongminh.modules.payment.model;

import jakarta.persistence.*;
import lombok.*;
import mstm.muasamthongminh.muasamthongminh.modules.payment.enums.ShippingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "shippings")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Shippings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Orders orderId;

    @Enumerated(EnumType.STRING)
    @JoinColumn(name = "shipping_status")
    private ShippingStatus shippingStatus;

    @Column(name = "shipping_fee")
    private BigDecimal shippingFee;

    @Column(name = "tracking_number")
    private String trackingNumber;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
