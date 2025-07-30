package mstm.muasamthongminh.muasamthongminh.modules.products.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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
    private String shortDescription;
    private String longDescription;
    private Long brandId;
    private String originalPrice;
    private String sellingPrice;
    private Long categoryId;
    private String mainImageUrl;
    private MultipartFile image;
    private ProductStatus status;
    private String slug;
    private Long createdByUserId;
    private Long updatedByUserId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
