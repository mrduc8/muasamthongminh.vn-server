package mstm.muasamthongminh.muasamthongminh.modules.brands.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import mstm.muasamthongminh.muasamthongminh.modules.brands.enums.BrandStatus;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BrandDto {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private String imageUrl;
    private MultipartFile image;
    private BrandStatus status;
    private Long createdByUserId;
    private Long updatedByUserId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
