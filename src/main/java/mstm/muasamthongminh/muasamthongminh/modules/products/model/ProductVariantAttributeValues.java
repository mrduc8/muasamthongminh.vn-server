package mstm.muasamthongminh.muasamthongminh.modules.products.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_variant_attribute_values")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariantAttributeValues {

    @EmbeddedId
    private PK id = new PK();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productVariantId")
    @JoinColumn(name = "product_variant_id")
    private ProductVariants productVariant;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("attributeValueId")
    @JoinColumn(name = "attribute_value_id")
    private AttributeValues attributeValue;

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PK implements java.io.Serializable {
        @Column(name = "product_variant_id")
        private Long productVariantId;

        @Column(name = "attribute_value_id")
        private Long attributeValueId;
    }
}
