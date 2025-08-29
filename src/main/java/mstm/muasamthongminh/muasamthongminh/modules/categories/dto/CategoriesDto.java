package mstm.muasamthongminh.muasamthongminh.modules.categories.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import mstm.muasamthongminh.muasamthongminh.common.enums.CategoryStatus;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoriesDto {
    private Long id;
    private String name;
    private String slug;
    private Long parentId;
    private Long sortOrder;
    private CategoryStatus status;
    private String imageUrl;
    private MultipartFile image;

    private String description;
    private String metaTitle;

    private String metaDescription;
    private Long createdByUserId;
    private Long updatedByUserId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CategoriesDto> children;
}
