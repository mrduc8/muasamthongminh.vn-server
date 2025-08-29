package mstm.muasamthongminh.muasamthongminh.modules.bankaccount.controller;

import lombok.RequiredArgsConstructor;
import mstm.muasamthongminh.muasamthongminh.modules.auth.security.CustomUserDetails;
import mstm.muasamthongminh.muasamthongminh.modules.bankaccount.dto.BankDto;
import mstm.muasamthongminh.muasamthongminh.modules.bankaccount.model.Bank;
import mstm.muasamthongminh.muasamthongminh.modules.bankaccount.service.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bank-account")
public class BankController {
    @Autowired
    private BankService bankService;

    @PostMapping("/create")
    public ResponseEntity<?> createBank(@RequestBody BankDto bankDto, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUser().getId();
        return bankService.createBank(userId, null, bankDto);
    }

    @PostMapping("/{shopId}/create-shop")
    public ResponseEntity<?> createShopBank(
            @PathVariable Long shopId,
            @RequestBody BankDto bankDto,
            Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUser().getId();
        return bankService.createBank(userId, shopId, bankDto);
    }

    @GetMapping
    public List<BankDto> getAllBank() {
        return bankService.getAllBank();
    }

    @GetMapping("/user")
    public List<BankDto> getBankByUser(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUser().getId();
        return bankService.getBankByUserId(userId);
    }

    // Lấy theo shopId (tài khoản shop)
    @GetMapping("/shop/{shopId}")
    public List<BankDto> getBankByShop(@PathVariable Long shopId) {
        return bankService.getBankByShopId(shopId);
    }
}
