package mstm.muasamthongminh.muasamthongminh.modules.bankaccount.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BankDto {
    private Long id;
    private Long userId;
    private String bankName;
    private String accountNumber;
    private String accountHolder;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
}
