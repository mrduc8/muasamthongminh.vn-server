package mstm.muasamthongminh.muasamthongminh.modules.products.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductImageDto {
    private Long id;
    private String imageUrl;
    private Long productId;
    private MultipartFile image;
    private Long sortOrder;
    private String altText;
    private LocalDateTime createdAt;
}
