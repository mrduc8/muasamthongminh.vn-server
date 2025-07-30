package mstm.muasamthongminh.muasamthongminh.modules.shoprequest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import mstm.muasamthongminh.muasamthongminh.common.enums.ShopStatus;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShopRequestsDto {
    private Long id;
    private Long userId;

    private String fullName;
    private String identityNumber;

    private String issuedDate;
    private String expDate;

    private String issuedPlace;
    private String businessName;
    private String taxCode;
    private String address;
    private String licenseFileUrl;
    private ShopStatus status;
    private String note;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
