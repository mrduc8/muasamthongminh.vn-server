package mstm.muasamthongminh.muasamthongminh.modules.products.model;

import jakarta.persistence.*;
import lombok.*;
import mstm.muasamthongminh.muasamthongminh.modules.auth.model.User;
import mstm.muasamthongminh.muasamthongminh.modules.brands.model.Brands;
import mstm.muasamthongminh.muasamthongminh.modules.categories.model.Categories;
import mstm.muasamthongminh.muasamthongminh.modules.products.enums.ProductStatus;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

@Entity
@Table(name = "products")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Products {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "sku_base")
    private String skuBase;

    @Column(name = "short_description")
    private String shortDescription;

    @Column(name = "long_description")
    private String longDescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brands brandId;

    @Column(name = "original_price")
    private String originalPrice;

    @Column(name = "selling _price")
    private String sellingPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Categories categoryId;

    @Column(name = "main_image_url")
    private String mainImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ProductStatus status = ProductStatus.DRAFT;

    @Column(name = "meta_title")
    private String metaTitle;

    @Column(name = "meta_description")
    private String metaDescription;

    @Column(name = "slug")
    private String slug;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdByUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by_user_id", nullable = false)
    private User updatedByUserId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    private String removeVietnameseAccents(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("");
    }

    @PrePersist
    @PreUpdate
    public void onSave() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }

        updatedAt = now;

        if (this.name != null && (this.slug == null || this.slug.isBlank())) {
            String slugified = removeVietnameseAccents(this.name)
                    .toLowerCase()
                    .replaceAll("[^a-z0-9\\s-]", "")
                    .replaceAll("\\s+", "-")
                    .replaceAll("-{2,}", "-")
                    .replaceAll("^-|-$", "");
            this.slug = slugified;
        }
    }
}
