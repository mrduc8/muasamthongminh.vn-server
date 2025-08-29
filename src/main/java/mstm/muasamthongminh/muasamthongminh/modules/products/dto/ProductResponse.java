package mstm.muasamthongminh.muasamthongminh.modules.products.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductResponse {
    private Long id;
    private String name;
    private Long categoryId;
    private Long brandId;
    private Long shopId;
    private String description;

    private String mainImageUrl;

    private String metaTitle;
    private String metaDescription;

    private String slug;
    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<ImageResponse> images;
    private List<VariantResponse> variants;

    @Data
    public static class ImageResponse {
        private Long id;
        private String imageUrl;
        private Long sortOrder;
        private String altText;
    }

    @Data
    public static class VariantResponse {
        private Long id;
        private String sku;
        private BigDecimal originalPrice;
        private BigDecimal salePrice;
        private Integer stockQuantity;
        private List<AttributeResponse> attributes;
    }

    @Data
    public static class AttributeResponse {
        private Long id;
        private String name;
        private String value;
        private String slug;
        private String colorCode;
        private String imageUrl;
    }
}
