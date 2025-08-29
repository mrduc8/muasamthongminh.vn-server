package mstm.muasamthongminh.muasamthongminh.modules.shop.controller;

import lombok.RequiredArgsConstructor;
import mstm.muasamthongminh.muasamthongminh.modules.auth.security.CustomUserDetails;
import mstm.muasamthongminh.muasamthongminh.modules.shop.dto.ShopDto;
import mstm.muasamthongminh.muasamthongminh.modules.shop.mapper.ShopMapper;
import mstm.muasamthongminh.muasamthongminh.modules.shop.model.Shop;
import mstm.muasamthongminh.muasamthongminh.modules.shop.service.ShopService;
import mstm.muasamthongminh.muasamthongminh.modules.shoprequest.model.ShopRequests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shop")
public class ShopController {
    @Autowired private ShopService shopService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createShop(@ModelAttribute ShopDto dto, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUser().getId();

        return shopService.createShop(userId, dto.getShopRequestsId(),dto);
    }

    @GetMapping
    public List<ShopDto> getAllShop() {
        return shopService.getAllShop();
    }

    @GetMapping("/{shopId}")
    public ResponseEntity<ShopDto> getShopById(@PathVariable Long shopId) {
        Shop shop = shopService.getShopById(shopId);
        ShopDto dto = ShopMapper.toDto(shop);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ShopDto> getShopByUserId(@PathVariable Long userId) {
        Shop shop = shopService.getShopByUserId(userId);
        ShopDto dto = ShopMapper.toDto(shop);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/me")
    public ResponseEntity<ShopDto> getMyShop(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUser().getId();

        Shop shop = shopService.getShopByUserId(userId);
        ShopDto dto = ShopMapper.toDto(shop);

        return ResponseEntity.ok(dto);
    }

}
