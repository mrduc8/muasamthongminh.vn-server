package mstm.muasamthongminh.muasamthongminh.modules.cart.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "cart_items",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_cart_variant", columnNames = {"cart_id", "product_variant_id"})
        },
        indexes = {
                @Index(name = "idx_ci_cart", columnList = "cart_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        // Liên kết với Cart
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "cart_id", nullable = false)
        private Cart cart;

        @Column(name = "shop_id")
        private Long shopId;

        @Column(name = "product_id")
        private Long productId;

        @Column(name = "product_variant_id", nullable = false)
        private Integer productVariantId;

        @Column(nullable = false)
        private Integer quantity;

        @Column(name="unit_price", precision = 38, scale = 2, nullable = false)
        private BigDecimal unitPrice;

        @Column(name="name_snapshot", nullable = false)
        private String nameSnapshot;

        @Column(name="image_url_snapshot")
        private String imageUrlSnapshot;

        @Column(name="variant_label_snapshot")
        private String variantLabelSnapshot;

        @Column(name="created_at", nullable = false)
        private LocalDateTime createdAt = LocalDateTime.now();

        @Column(name="updated_at", nullable = false)
        private LocalDateTime updatedAt = LocalDateTime.now();

        @PrePersist
        public void prePersist() {
                final LocalDateTime now = LocalDateTime.now();
                if (createdAt == null) createdAt = now;
                if (updatedAt == null) updatedAt = now;

                if (quantity == null) quantity = 1;
                if (unitPrice == null) unitPrice = BigDecimal.ZERO;
                if (nameSnapshot == null) nameSnapshot = "";
        }

        @PreUpdate
        public void preUpdate() {
                updatedAt = LocalDateTime.now();
        }
}
