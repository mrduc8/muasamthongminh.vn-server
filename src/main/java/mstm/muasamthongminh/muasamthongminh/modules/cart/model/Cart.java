package mstm.muasamthongminh.muasamthongminh.modules.cart.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "session_id", length = 64)
    private String sessionId;

    @Builder.Default
    @Column(length = 20, nullable = false)
    private String status = "ACTIVE";

    @Builder.Default
    @Column(length = 10, nullable = false)
    private String currency = "VND";

    @Builder.Default
    @Column(precision = 38, scale = 2, nullable = false)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Builder.Default
    @Column(name="discount_total", precision = 38, scale = 2, nullable = false)
    private BigDecimal discountTotal = BigDecimal.ZERO;

    @Builder.Default
    @Column(name="shipping_total", precision = 38, scale = 2, nullable = false)
    private BigDecimal shippingTotal = BigDecimal.ZERO;

    @Builder.Default
    @Column(name="tax_total", precision = 38, scale = 2, nullable = false)
    private BigDecimal taxTotal = BigDecimal.ZERO;

    @Builder.Default
    @Column(name="grand_total", precision = 38, scale = 2, nullable = false)
    private BigDecimal grandTotal = BigDecimal.ZERO;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CartItem> items = new ArrayList<>();

    @Column(name="created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name="updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PrePersist
    public void prePersist() {
        final LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;

        // đảm bảo các field NOT NULL luôn có giá trị khi dùng builder
        if (status == null) status = "ACTIVE";
        if (currency == null) currency = "VND";
        if (subtotal == null) subtotal = BigDecimal.ZERO;
        if (discountTotal == null) discountTotal = BigDecimal.ZERO;
        if (shippingTotal == null) shippingTotal = BigDecimal.ZERO;
        if (taxTotal == null) taxTotal = BigDecimal.ZERO;
        if (grandTotal == null) grandTotal = BigDecimal.ZERO;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
