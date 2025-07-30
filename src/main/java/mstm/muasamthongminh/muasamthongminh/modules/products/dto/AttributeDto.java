package mstm.muasamthongminh.muasamthongminh.modules.products.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import mstm.muasamthongminh.muasamthongminh.modules.products.enums.AttributeStatus;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttributeDto {
    private Long id;
    private String name;
    private String slug;
    private AttributeStatus status;
    private String description;
    private Long sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
