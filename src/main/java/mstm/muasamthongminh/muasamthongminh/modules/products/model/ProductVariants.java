package mstm.muasamthongminh.muasamthongminh.modules.products.model;

import jakarta.persistence.*;
import lombok.*;
import mstm.muasamthongminh.muasamthongminh.modules.products.enums.ProductVariantStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_variants")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductVariants {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Products productId;

    @Column(name = "sku")
    private String sku;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "quantity_in_stock")
    private Long quantityInStock;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "weight")
    private BigDecimal weight;

    @Enumerated(EnumType.STRING)
    @JoinColumn(name = "status")
    private ProductVariantStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    private void onSave(){
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
}
