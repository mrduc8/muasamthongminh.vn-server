package mstm.muasamthongminh.muasamthongminh.modules.shop.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import mstm.muasamthongminh.muasamthongminh.common.enums.ShopStatus;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.*;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShopDto {
    private Long id;
    private Long userId;
    private Long shopRequestsId;
    private String shopName;
    private String description;
    private String logoUrl;
    private String bannerUrl;

    private MultipartFile logoImage;
    private MultipartFile bannerImage;

    private String address;
    private String provinceCode;
    private String districtCode;
    private String wardCode;
    private ShopStatus status;
    private LocalDateTime createAt;
}
