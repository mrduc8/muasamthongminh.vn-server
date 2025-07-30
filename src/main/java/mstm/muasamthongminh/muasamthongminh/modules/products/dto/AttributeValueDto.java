package mstm.muasamthongminh.muasamthongminh.modules.products.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttributeValueDto {
    private Long id;
    private Long attributeId;
    private String value;
    private String slug;
    private String colorCode;
    private String imageUrl;
    private MultipartFile image;
    private Long sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
