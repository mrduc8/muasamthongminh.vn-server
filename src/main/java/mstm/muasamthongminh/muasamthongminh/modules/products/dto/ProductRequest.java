package mstm.muasamthongminh.muasamthongminh.modules.products.dto;

import lombok.Data;
import mstm.muasamthongminh.muasamthongminh.modules.products.enums.ProductStatus;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductRequest {
    private String name;
    private Long categoryId;
    private Long brandId;
    private Long shopId;
    private String description;

    private String mainImageUrl;
    private MultipartFile mainImage;

    private String metaTitle;
    private String metaDescription;

    private ProductStatus status;

    private List<ImageRequest> images;
    private List<VariantRequest> variants;

    @Data
    public static class ImageRequest {
        private String imageUrl;
        private MultipartFile image;

        private Long sortOrder;
        private String altText;
    }

    @Data
    public static class VariantRequest {
        private String sku;
        private BigDecimal originalPrice;
        private BigDecimal salePrice;
        private Integer stockQuantity;
        private List<AttributeRequest> attributes;
    }

    @Data
    public static class AttributeRequest {
        private String name;
        private String value;
        private String slug;
        private String colorCode;

        private String imageUrl;
        private MultipartFile image;
    }
}
