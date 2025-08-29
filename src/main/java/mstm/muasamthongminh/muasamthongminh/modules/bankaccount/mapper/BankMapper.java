package mstm.muasamthongminh.muasamthongminh.modules.bankaccount.mapper;

import mstm.muasamthongminh.muasamthongminh.modules.auth.model.User;
import mstm.muasamthongminh.muasamthongminh.modules.bankaccount.dto.BankDto;
import mstm.muasamthongminh.muasamthongminh.modules.bankaccount.model.Bank;
import mstm.muasamthongminh.muasamthongminh.modules.shop.model.Shop;

public class BankMapper {
    public static Bank toEntity(BankDto dto, User user, Shop shop){
        if(dto == null) return null;

        return Bank.builder()
                .id(dto.getId())
                .user(user)
                .shop(shop)
                .bankName(dto.getBankName())
                .accountHolder(dto.getAccountHolder())
                .accountNumber(String.valueOf(dto.getAccountNumber()))
                .createAt(dto.getCreateDate())
                .updateAt(dto.getUpdateDate())
                .build();
    }

    public static BankDto toDto(Bank bank){
        if(bank == null) return null;

        BankDto dto = new BankDto();
        dto.setId(bank.getId());
        dto.setUserId(bank.getUser() != null ? bank.getUser().getId() : null);
        dto.setShopId(bank.getShop() != null ? bank.getShop().getId() : null);
        dto.setBankName(bank.getBankName());
        dto.setAccountHolder(bank.getAccountHolder());
        dto.setAccountNumber(bank.getAccountNumber());
        dto.setCreateDate(bank.getCreateAt());
        dto.setUpdateDate(bank.getUpdateAt());
        return dto;
    }
}
