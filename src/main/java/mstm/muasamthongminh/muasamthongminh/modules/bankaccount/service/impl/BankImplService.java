package mstm.muasamthongminh.muasamthongminh.modules.bankaccount.service.impl;

import lombok.RequiredArgsConstructor;
import mstm.muasamthongminh.muasamthongminh.common.mailcontent.WellComeToCreateShop;
import mstm.muasamthongminh.muasamthongminh.common.service.MailService;
import mstm.muasamthongminh.muasamthongminh.modules.auth.model.User;
import mstm.muasamthongminh.muasamthongminh.modules.auth.repository.AuthUserRepository;
import mstm.muasamthongminh.muasamthongminh.modules.bankaccount.dto.BankDto;
import mstm.muasamthongminh.muasamthongminh.modules.bankaccount.mapper.BankMapper;
import mstm.muasamthongminh.muasamthongminh.modules.bankaccount.model.Bank;
import mstm.muasamthongminh.muasamthongminh.modules.bankaccount.repository.BankRepsitory;
import mstm.muasamthongminh.muasamthongminh.modules.bankaccount.service.BankService;
import mstm.muasamthongminh.muasamthongminh.modules.shop.model.Shop;
import mstm.muasamthongminh.muasamthongminh.modules.shop.repository.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BankImplService implements BankService {
    @Autowired private BankRepsitory bankRepsitory;
    @Autowired private AuthUserRepository authUserRepository;
    @Autowired private MailService mailService;
    @Autowired private ShopRepository shopRepository;

    @Override
    public ResponseEntity<?> createBank(Long userId,  Long shopId, BankDto bankDto) {
        User user = authUserRepository.findById(userId).orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

//        boolean exists = bankRepsitory.existsByUserId(userId);
//        if (exists) {
//            return ResponseEntity.ok(Map.of("message", "Bạn đã thêm một tài khoản. Vui lòng liên hệ Admin"));
//        }

        Shop shop = null;
        if (shopId != null) {
            shop = shopRepository.findById(shopId)
                    .orElseThrow(() -> new RuntimeException("Shop không tồn tại"));
        }

        Bank req = BankMapper.toEntity(bankDto, user, shop);
        Bank savedBank = bankRepsitory.save(req);

        // Gửi xác nhận về cho người dùng
        String content = WellComeToCreateShop.buildBankCreationContent(user.getUsername(), bankDto.getAccountHolder(), bankDto.getAccountNumber());
        mailService.sendSimpleMail(user.getEmail(), "Xác nhận thêm tài khoản ngân hàng", content);

        return ResponseEntity.ok(BankMapper.toDto(savedBank));
    }

    @Override
    public List<BankDto> getAllBank(){
        List<Bank> banks = bankRepsitory.findAll();
        return banks.stream().map(BankMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<BankDto> getBankByUserId(Long userId) {
        List<Bank> banks = bankRepsitory.findByUserId(userId);
        return banks.stream().map(BankMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<BankDto> getBankByShopId(Long shopId) {
        List<Bank> banks = bankRepsitory.findByShopId(shopId);
        return banks.stream().map(BankMapper::toDto).collect(Collectors.toList());
    }
}
