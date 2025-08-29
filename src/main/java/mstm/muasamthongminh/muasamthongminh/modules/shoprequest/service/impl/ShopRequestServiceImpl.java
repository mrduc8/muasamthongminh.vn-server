package mstm.muasamthongminh.muasamthongminh.modules.shoprequest.service.impl;

import jakarta.transaction.Transactional;
import mstm.muasamthongminh.muasamthongminh.common.enums.ShopStatus;
import mstm.muasamthongminh.muasamthongminh.common.exception.ApiException;
import mstm.muasamthongminh.muasamthongminh.common.mailcontent.WellComeToCreateShop;
import mstm.muasamthongminh.muasamthongminh.common.service.MailService;
import mstm.muasamthongminh.muasamthongminh.modules.auth.model.User;
import mstm.muasamthongminh.muasamthongminh.modules.auth.repository.AuthUserRepository;
import mstm.muasamthongminh.muasamthongminh.modules.bankaccount.dto.BankDto;
import mstm.muasamthongminh.muasamthongminh.modules.bankaccount.mapper.BankMapper;
import mstm.muasamthongminh.muasamthongminh.modules.bankaccount.model.Bank;
import mstm.muasamthongminh.muasamthongminh.modules.bankaccount.repository.BankRepsitory;
import mstm.muasamthongminh.muasamthongminh.modules.shop.mapper.ShopMapper;
import mstm.muasamthongminh.muasamthongminh.modules.shop.model.Shop;
import mstm.muasamthongminh.muasamthongminh.modules.shop.repository.ShopRepository;
import mstm.muasamthongminh.muasamthongminh.modules.shoprequest.dto.ShopRequestsDetailDto;
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
    @Autowired
    private BankRepsitory bankRepsitory;
    @Autowired
    private MailService mailService;


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

        // Đồng bộ Shop (nếu có)
        Optional<Shop> shopOpt = shopRepository.findByShopRequestsId(requestId);
        shopOpt.ifPresent(shop -> {
            shop.setStatus(ShopStatus.APPROVED);
            shopRepository.save(shop);
        });

        // Lưu request
        ShopRequests updatedRequest = shopRequestsRepository.save(request);

        // ===== Gửi mail thông báo phê duyệt cho user =====
        // Lấy user từ entity (ưu tiên) hoặc từ repo (fallback)
        User u = updatedRequest.getUser();
        if (u == null && updatedRequest.getUser().getId() != null) {
            u = authUserRepository.findById(updatedRequest.getUser().getId()).orElse(null);
        }

        if (u != null && u.getEmail() != null && !u.getEmail().isBlank()) {
            try {
                // Chọn tên hiển thị: fullName (nếu có) fallback về username
                String displayName = updatedRequest.getFullName() != null && !updatedRequest.getFullName().isBlank()
                        ? updatedRequest.getFullName()
                        : u.getUsername();

                String content = WellComeToCreateShop.buildShopApprovedContent(
                        u.getUsername(),          // hoặc displayName tuỳ template bạn muốn
                        displayName               // tên hiển thị trong nội dung mail
                );

                mailService.sendSimpleMail(
                        u.getEmail(),
                        "Yêu cầu mở shop đã được phê duyệt",
                        content
                );
            } catch (Exception ex) {
                // Không throw để tránh rollback nghiệp vụ chỉ vì lỗi email
                // TODO: log.warn("Gửi mail phê duyệt thất bại cho {}", u.getEmail(), ex);
            }
        }

        return ShopRequestMapper.toDto(updatedRequest);
    }


    @Override
    public ShopRequestsDto rejectRequest(Long requestId, String reason) {
        ShopRequests req = shopRequestsRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Yêu cầu không tồn tại"));

        req.setStatus(ShopStatus.REJECTED);
        req.setNote(reason);
        req.setUpdatedAt(LocalDateTime.now());

        Optional<Shop> shopOpt = shopRepository.findByShopRequestsId(requestId);
        shopOpt.ifPresent(shop -> {
            shop.setStatus(ShopStatus.REJECTED);
            shopRepository.save(shop);
        });

        ShopRequests saved = shopRequestsRepository.save(req);

        try {
            User u = saved.getUser();
            if (u != null && u.getEmail() != null && !u.getEmail().isBlank()) {
                String subject = "Yêu cầu mở shop đã bị từ chối";
                String content = """
                    Xin chào %s,

                    Rất tiếc, yêu cầu mở shop của bạn đã bị từ chối.

                    Lý do: %s

                    Vui lòng cập nhật lại thông tin và gửi yêu cầu mới nếu cần.
                    Trân trọng.
                    """.formatted(
                        (saved.getFullName() != null && !saved.getFullName().isBlank())
                                ? saved.getFullName()
                                : u.getUsername(),
                        (reason == null || reason.isBlank()) ? "Không có ghi chú" : reason
                );
                mailService.sendSimpleMail(u.getEmail(), subject, content);
            }
        } catch (Exception ignore) {
            throw new RuntimeException(ignore);
        }

        return ShopRequestMapper.toReject(saved);
    }


    // Check trạng thái đăng ký cửa hàng!
    @Override
    public Optional<ShopRequestsDto> getShopRequestUserId(Long userId) {
        return shopRequestsRepository.findByUserId(userId)
                .map(ShopRequestMapper::toDto);
    }

    // Lấy tất cả thông tin gủi shoprequest
    @Override
    public Optional<ShopRequestsDetailDto> getDetailShopRequest(Long userId) {
        Optional<ShopRequests> shopRequestsOpt = shopRequestsRepository.findByUserId(userId);
        if (shopRequestsOpt.isEmpty()){
            return Optional.empty();
        }

        ShopRequestsDetailDto detailDto =  new ShopRequestsDetailDto();
        ShopRequests shopRequests = shopRequestsOpt.get();

        detailDto.setShop_requests(ShopRequestMapper.toDto(shopRequests));

        Optional<Shop> shopOpt = shopRepository.findByShopRequestsId(shopRequests.getId());
        if (shopOpt.isPresent()){
            Shop shop = shopOpt.get();
            detailDto.setShop(ShopMapper.toDto(shop));

            List<Bank> bankOpt = bankRepsitory.findByShopId(shop.getId());
            if (!bankOpt.isEmpty()){
                detailDto.setBank(bankOpt.stream().map(BankMapper::toDto).collect(Collectors.toList()));
            }
        }
        return Optional.of(detailDto);
    }

    @Override
    public List<ShopRequestsDto> approveAllPending() {
        List<ShopRequests> pending = shopRequestsRepository.findByStatus(ShopStatus.PENDING);
        if (pending.isEmpty()) return List.of();

        List<Long> requestIds = pending.stream().map(ShopRequests::getId).toList();

        // Nếu Shop liên kết qua quan hệ:
        List<Shop> shops = shopRepository.findByShopRequestsIdIn(requestIds);
        Map<Long, Shop> shopByRequestId = shops.stream()
                .collect(Collectors.toMap(s -> s.getShopRequests().getId(), s -> s));

        LocalDateTime now = LocalDateTime.now();
        for (ShopRequests req : pending) {
            req.setStatus(ShopStatus.APPROVED);
            req.setUpdatedAt(now);

            Shop related = shopByRequestId.get(req.getId());
            if (related != null) {
                related.setStatus(ShopStatus.APPROVED);
            }
        }

        if (!shops.isEmpty()) {
            shopRepository.saveAll(shops);
        }
        List<ShopRequests> saved = shopRequestsRepository.saveAll(pending);

        for (ShopRequests req : saved) {
            User u = req.getUser();
            if (u != null && u.getEmail() != null) {
                try {
                    String content = WellComeToCreateShop.buildShopApprovedContent(
                            u.getUsername(),
                            req.getFullName()
                    );
                    mailService.sendSimpleMail(
                            u.getEmail(),
                            "Yêu cầu mở shop đã được phê duyệt",
                            content
                    );
                } catch (Exception ex) {
                    new RuntimeException(ex);
                }
            }
        }

        return saved.stream().map(ShopRequestMapper::toDto).toList();
    }
}
