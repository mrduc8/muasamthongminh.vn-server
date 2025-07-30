package mstm.muasamthongminh.muasamthongminh.modules.shoprequest.service.impl;

import jakarta.transaction.Transactional;
import mstm.muasamthongminh.muasamthongminh.common.enums.ShopStatus;
import mstm.muasamthongminh.muasamthongminh.common.exception.ApiException;
import mstm.muasamthongminh.muasamthongminh.modules.auth.model.User;
import mstm.muasamthongminh.muasamthongminh.modules.auth.repository.AuthUserRepository;
import mstm.muasamthongminh.muasamthongminh.modules.shop.model.Shop;
import mstm.muasamthongminh.muasamthongminh.modules.shop.repository.ShopRepository;
import mstm.muasamthongminh.muasamthongminh.modules.shoprequest.dto.ShopRequestsDto;
import mstm.muasamthongminh.muasamthongminh.modules.shoprequest.mapper.ShopRequestMapper;
import mstm.muasamthongminh.muasamthongminh.modules.shoprequest.model.ShopRequests;
import mstm.muasamthongminh.muasamthongminh.modules.shoprequest.repository.ShopRequestsRepository;
import mstm.muasamthongminh.muasamthongminh.modules.shoprequest.service.ShopRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ShopRequestServiceImpl implements ShopRequestService {

    @Autowired
    private ShopRequestsRepository shopRequestsRepository;
    @Autowired
    private AuthUserRepository authUserRepository;
    @Autowired
    private ShopRepository shopRepository;

    // Tạo yêu cầu mở shop mới
    @Override
    public ResponseEntity<?> createRequest(Long userId, ShopRequestsDto dto) {
        User user = authUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        // Kiểm tra user !!user chỉ được tạo một lần
        boolean exists = shopRequestsRepository.existsByUserId(userId);
        if (exists) {
            return ResponseEntity.ok(Map.of("message", "Bạn đã gửi yêu cầu trước đó. Yêu cầu sẽ được xét duyệt trong 1 - 2 ngày"));
        }

        // Lưu dữ liệu
        ShopRequests requests = ShopRequestMapper.toEntity(dto, user);
        ShopRequests savedRequest = shopRequestsRepository.save(requests);

        return ResponseEntity.ok(ShopRequestMapper.toDto(savedRequest));
    }

    // Lấy tất cả thông tin đăng ký trạng thái pending
    @Override
    public List<ShopRequestsDto> getPendingRequests() {
        List<ShopRequests> requests = shopRequestsRepository.findByStatus(ShopStatus.PENDING);
        return requests.stream().map(ShopRequestMapper::toDto).collect(Collectors.toList());
    }

    // Lấy tất cả danh sách shop đăng ký
    public List<ShopRequestsDto> getAllRequests() {
        List<ShopRequests> requests = shopRequestsRepository.findAll();
        return requests.stream().map(ShopRequestMapper::toDto).collect(Collectors.toList());
    }

    // Phê duyệt yêu cầu
    @Override
    public ShopRequestsDto approveRequest(Long requestId) {
        ShopRequests request = shopRequestsRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Yêu cầu không tồn tại"));

        request.setStatus(ShopStatus.APPROVED);
        request.setUpdatedAt(LocalDateTime.now());

        // Đồng bộ với shop-request
        Optional<Shop> shopOpt = shopRepository.findByShopRequestsId(requestId);
        shopOpt.ifPresent(shop -> {
            shop.setStatus(ShopStatus.APPROVED);
            shopRepository.save(shop);
        });

        ShopRequests updatedRequest = shopRequestsRepository.save(request);
        return ShopRequestMapper.toDto(updatedRequest);
    }

    // Từ chối yêu cầu và ghi chú
    @Override
    public ShopRequestsDto rejectRequest(Long requestId, String reason) {
        ShopRequests requests = shopRequestsRepository.findById(requestId).orElseThrow(() -> new RuntimeException("Yêu cầu không tồn tại"));
        requests.setStatus(ShopStatus.REJECTED);
        requests.setNote(reason);
        requests.setUpdatedAt(LocalDateTime.now());

        ShopRequests updatedRequest = shopRequestsRepository.save(requests);
        return ShopRequestMapper.toReject(updatedRequest);
    }
}
