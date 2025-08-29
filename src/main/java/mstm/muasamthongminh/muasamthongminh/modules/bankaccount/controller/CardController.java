package mstm.muasamthongminh.muasamthongminh.modules.bankaccount.controller;

import lombok.RequiredArgsConstructor;
import mstm.muasamthongminh.muasamthongminh.modules.auth.security.CustomUserDetails;
import mstm.muasamthongminh.muasamthongminh.modules.bankaccount.dto.CardDto;
import mstm.muasamthongminh.muasamthongminh.modules.bankaccount.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/card")
public class CardController {
    @Autowired private CardService cardService;

    @PostMapping
    public ResponseEntity<?> createCard(@RequestBody CardDto dto, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUser().getId();
        return cardService.createCard(dto, userId);
    }

    @GetMapping
    public List<CardDto> getAllCards(){return cardService.getAllCard();}
}
