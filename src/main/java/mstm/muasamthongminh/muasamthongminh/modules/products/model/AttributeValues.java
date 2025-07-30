package mstm.muasamthongminh.muasamthongminh.modules.products.model;

import jakarta.persistence.*;
import lombok.*;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

@Entity
@Table(name = "attribute_vales")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttributeValues {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_id", nullable = false)
    private Attributes attributeId;

    @Column(name = "value")
    private String value;

    @Column(name = "slug")
    private String slug;

    @Column(name = "color_code")
    private String colorCode;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "sort_order")
    private Long sortOrder;

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

        if (this.value != null && (this.slug == null || this.slug.isBlank())) {
            String slugified = removeVietnameseAccents(this.value)
                    .toLowerCase()
                    .replaceAll("[^a-z0-9\\s-]", "")
                    .replaceAll("\\s+", "-")
                    .replaceAll("-{2,}", "-")
                    .replaceAll("^-|-$", "");
            this.slug = slugified;
        }
    }
}
