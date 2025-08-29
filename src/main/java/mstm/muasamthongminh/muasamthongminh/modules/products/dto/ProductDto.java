package mstm.muasamthongminh.muasamthongminh.modules.products.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import lombok.Data;
import mstm.muasamthongminh.muasamthongminh.modules.products.enums.ProductStatus;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDto {
    private Long id;
    private String name;
    private String skuBase;
    private Long brandId;
    private Long categoryId;
    private Long shopId;
    private String description;
    private String mainImageUrl;
    private MultipartFile image;
    private ProductStatus status;
    private String slug;
    private String metaTitle;
    private String metaDescription;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
