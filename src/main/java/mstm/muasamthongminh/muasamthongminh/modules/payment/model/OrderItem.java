package mstm.muasamthongminh.muasamthongminh.modules.payment.model;

import jakarta.persistence.*;
import lombok.*;
import mstm.muasamthongminh.muasamthongminh.modules.products.model.ProductVariants;
import mstm.muasamthongminh.muasamthongminh.modules.products.model.Products;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Orders orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Products productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id", nullable = false)
    private ProductVariants productVariantId;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    @Column(name = "line_total")
    private BigDecimal lineTotal;

    @Column(name = "name_snapshot")
    private String nameSnapshot;

    @Column(name = "image_url_snapshot")
    private String imageUrlSnapshot;

    @Column(name = "variant_label_snapshot")
    private String variantLabelSnapshot;
}
