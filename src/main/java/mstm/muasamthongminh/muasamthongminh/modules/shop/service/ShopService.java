package mstm.muasamthongminh.muasamthongminh.modules.shop.service;

import mstm.muasamthongminh.muasamthongminh.modules.shop.dto.ShopDto;
import mstm.muasamthongminh.muasamthongminh.modules.shop.model.Shop;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ShopService {
    // Tạo shop mới từ người dùng
    ResponseEntity<?> createShop(Long userId,Long shopRequestsId, ShopDto shopDto);

    // Lấy tất cả thông tin shop
    List<ShopDto> getAllShop();
}
