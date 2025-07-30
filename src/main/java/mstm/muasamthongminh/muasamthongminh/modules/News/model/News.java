package mstm.muasamthongminh.muasamthongminh.modules.News.model;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

@Entity
@Table(name = "blog_posts")
@Data
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;


    private String slug;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String image;
    private String status;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @PrePersist
    @PreUpdate
    public void generateSlug() {
        if (this.title != null && (this.slug == null || this.slug.isBlank())) {
            String slugified = removeVietnameseAccents(this.title)
                    .toLowerCase()
                    .replaceAll("[^a-z0-9\\s-]", "")
                    .replaceAll("\\s+", "-")
                    .replaceAll("-{2,}", "-")
                    .replaceAll("^-|-$", "");
            this.slug = slugified;
        }
    }

    private String removeVietnameseAccents(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("");
    }
}
