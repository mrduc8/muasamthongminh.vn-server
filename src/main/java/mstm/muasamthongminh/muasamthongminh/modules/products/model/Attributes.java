package mstm.muasamthongminh.muasamthongminh.modules.products.model;

import jakarta.persistence.*;
import lombok.*;
import mstm.muasamthongminh.muasamthongminh.modules.products.enums.AttributeStatus;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

@Entity
@Table(name = "attribues")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Attributes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "slug")
    private String slug;

    @Enumerated(EnumType.STRING)
    @JoinColumn(name = "status")
    private AttributeStatus status;

    @Column(name = "description")
    private String description;

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
