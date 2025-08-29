package mstm.muasamthongminh.muasamthongminh.modules.shoprequest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import mstm.muasamthongminh.muasamthongminh.modules.bankaccount.dto.BankDto;
import mstm.muasamthongminh.muasamthongminh.modules.shop.dto.ShopDto;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShopRequestsDetailDto {
    public ShopRequestsDto shop_requests;
    public ShopDto shop;
    public List<BankDto> bank;
}
