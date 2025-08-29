package mstm.muasamthongminh.muasamthongminh.modules.bankaccount.service.impl;

import lombok.RequiredArgsConstructor;
import mstm.muasamthongminh.muasamthongminh.common.mailcontent.NotificationAddCard;
import mstm.muasamthongminh.muasamthongminh.common.service.MailService;
import mstm.muasamthongminh.muasamthongminh.modules.auth.model.User;
import mstm.muasamthongminh.muasamthongminh.modules.auth.repository.AuthUserRepository;
import mstm.muasamthongminh.muasamthongminh.modules.bankaccount.dto.CardDto;
import mstm.muasamthongminh.muasamthongminh.modules.bankaccount.mapper.CardMapper;
import mstm.muasamthongminh.muasamthongminh.modules.bankaccount.model.Card;
import mstm.muasamthongminh.muasamthongminh.modules.bankaccount.repository.CardRepository;
import mstm.muasamthongminh.muasamthongminh.modules.bankaccount.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardImplService implements CardService {

    private final CardRepository cardRepository;
    private final AuthUserRepository authUserRepository;
    private final MailService mailService;

    @Override
    public ResponseEntity<?> createCard(CardDto dto, Long userId) {
        User user = authUserRepository.findById(userId).orElseThrow(() -> new RuntimeException("Người dùng không tồn tại!"));

        Card req = CardMapper.toEntity(dto, user);
        Card savedCard = cardRepository.save(req);

        String content = NotificationAddCard.buildCardCreationContent(user.getUsername(), dto.getCardNumber(), dto.getCardHolderName());
        mailService.sendSimpleMail(user.getEmail(), "Xác nhận thêm thẻ ngân hàng", content);
        return ResponseEntity.ok().body(CardMapper.toDto(savedCard));
    }

    @Override
    public List<CardDto> getAllCard() {
       List<Card> cards = cardRepository.findAll();
       return cards.stream().map(CardMapper::toDto).collect(Collectors.toList());
    }
}