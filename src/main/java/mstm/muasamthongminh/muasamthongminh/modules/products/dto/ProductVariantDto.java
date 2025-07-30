package mstm.muasamthongminh.muasamthongminh.modules.products.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import mstm.muasamthongminh.muasamthongminh.modules.products.enums.ProductVariantStatus;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductVariantDto {
    private Long id;
    private Long productId;
    private String sku;
    private BigDecimal price;
    private Long quantityInStock;
    private String imageUrl;
    private MultipartFile image;
    private BigDecimal weight;
    private ProductVariantStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
