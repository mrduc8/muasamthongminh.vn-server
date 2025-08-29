package mstm.muasamthongminh.muasamthongminh.modules.bankaccount.service;

import mstm.muasamthongminh.muasamthongminh.modules.bankaccount.dto.CardDto;
import mstm.muasamthongminh.muasamthongminh.modules.bankaccount.model.Card;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CardService {
    // Tạo thẻ ngân hàng
    ResponseEntity<?> createCard(CardDto dto, Long userId);

    // Lấy tất cả danh sách dto
    List<CardDto> getAllCard();


}
