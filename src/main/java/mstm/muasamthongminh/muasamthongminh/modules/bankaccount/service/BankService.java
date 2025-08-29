package mstm.muasamthongminh.muasamthongminh.modules.bankaccount.service;

import mstm.muasamthongminh.muasamthongminh.modules.bankaccount.dto.BankDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface BankService {
    // Tạo ngân hàng từ người dùng.
    ResponseEntity<?> createBank(Long userId, Long shopId, BankDto bankDto);

    // Lấy tất cả tài khoản ngân hàng
    List<BankDto> getAllBank();

    // Lấy theo userId
    List<BankDto> getBankByUserId(Long userId);

    // Lấy theo shopId
    List<BankDto> getBankByShopId(Long shopId);
}
