package mstm.muasamthongminh.muasamthongminh.modules.shop.service.impl;

import lombok.RequiredArgsConstructor;
import mstm.muasamthongminh.muasamthongminh.common.enums.Role;
import mstm.muasamthongminh.muasamthongminh.common.service.ImageUploadService;
import mstm.muasamthongminh.muasamthongminh.modules.auth.model.Roles;
import mstm.muasamthongminh.muasamthongminh.modules.auth.model.User;
import mstm.muasamthongminh.muasamthongminh.modules.auth.repository.AuthUserRepository;
import mstm.muasamthongminh.muasamthongminh.modules.auth.repository.RoleRepository;
import mstm.muasamthongminh.muasamthongminh.modules.shop.dto.ShopDto;
import mstm.muasamthongminh.muasamthongminh.modules.shop.mapper.ShopMapper;
import mstm.muasamthongminh.muasamthongminh.modules.shop.model.Shop;
import mstm.muasamthongminh.muasamthongminh.modules.shop.repository.ShopRepository;
import mstm.muasamthongminh.muasamthongminh.modules.shop.service.ShopService;
import mstm.muasamthongminh.muasamthongminh.modules.shoprequest.model.ShopRequests;
import mstm.muasamthongminh.muasamthongminh.modules.shoprequest.repository.ShopRequestsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private AuthUserRepository authUserRepository;
    @Autowired
    private ShopRequestsRepository shopRequestsRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ImageUploadService imageUploadService;

    // Tạo yêu cầu mở shop mới
    @Override
    public ResponseEntity<?> createShop(Long userId, Long shopRequestsId, ShopDto shopDto) {
        User user = authUserRepository.findById(userId).orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
        ShopRequests shopRequests = shopRequestsRepository.findById(shopRequestsId).orElseThrow(() -> new RuntimeException("Hồ sơ không hợp lệ"));

        // Kiểm tra user !!user chỉ được tạo một lần
        boolean exists = shopRepository.existsByUserId(userId);
        if (exists) {
            return ResponseEntity.ok(Map.of("message", "Bạn đã gửi yêu cầu trước đó. Yêu cầu sẽ được xét duyệt trong 1 - 2 ngày"));
        }

        // Upload logo
        if (shopDto.getLogoImage() != null && !shopDto.getLogoImage().isEmpty()) {
            try {
                String logoUrl = imageUploadService.uploadImage(shopDto.getLogoImage());
                shopDto.setLogoUrl(logoUrl);
            } catch (IOException e) {
                throw new RuntimeException("Lỗi upload logo: " + e.getMessage());
            }
        }

        // Upload banner
        if (shopDto.getBannerImage() != null && !shopDto.getBannerImage().isEmpty()) {
            try {
                String bannerUrl = imageUploadService.uploadImage(shopDto.getBannerImage());
                shopDto.setBannerUrl(bannerUrl);
            } catch (IOException e) {
                throw new RuntimeException("Lỗi upload banner: " + e.getMessage());
            }
        }

        Shop requests = ShopMapper.toEntity(shopDto, user, shopRequests);
        Shop saveRequests = shopRepository.save(requests);

        // Cập nhập role người dùng
        if (user.getRoles().stream().noneMatch(r -> r.getName() == Role.SELLER)) {
            Roles sellerRole = roleRepository.findByName(Role.SELLER)
                    .orElseThrow(() -> new RuntimeException("Vai trò người dùng không tồn tại."));

            user.getRoles().add(sellerRole);
            authUserRepository.save(user);
        }


        return ResponseEntity.ok(ShopMapper.toDto(saveRequests));
    }

    // Lấy tất cả thông tin shop
    @Override
    public List<ShopDto> getAllShop() {
        List<Shop> shops = shopRepository.findAll();
        return shops.stream().map(ShopMapper::toDto).collect(Collectors.toList());
    }

}
