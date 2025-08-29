package mstm.muasamthongminh.muasamthongminh.modules.shoprequest.controller;

import mstm.muasamthongminh.muasamthongminh.modules.auth.security.CustomUserDetails;
import mstm.muasamthongminh.muasamthongminh.modules.shoprequest.dto.ShopRequestsDetailDto;
import mstm.muasamthongminh.muasamthongminh.modules.shoprequest.dto.ShopRequestsDto;
import mstm.muasamthongminh.muasamthongminh.modules.shoprequest.service.ShopRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.attribute.UserPrincipal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/shop-requests")
public class ShopRequestController {

    @Autowired
    private ShopRequestService shopRequestService;

    // Lấy check trạng thái shop của user hiện tại
    @GetMapping("/my-shop")
    public ResponseEntity<?> getMyShopRequests(@AuthenticationPrincipal(expression = "id") Long userId) {
        Optional<ShopRequestsDto> requestOtp = shopRequestService.getShopRequestUserId(userId);
        if (requestOtp.isPresent()) {
            return ResponseEntity.ok(requestOtp.get());
        } else {
            return ResponseEntity.ok().body(
                    new Object() {
                        public final String message = "User chưa đăng ký shop nào";
                        public final String status = "NONE";

                    }
            );
        }
    }

    //
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getShopRequestDetail(@PathVariable("id") Long userId) {

        Optional<ShopRequestsDetailDto> detailOpt = shopRequestService.getDetailShopRequest(userId);

        if (detailOpt.isEmpty()) {
            return ResponseEntity.ok().body(
                    new Object() {
                        public final String message = "Không tìm thấy yêu cầu shop nào cho userId = " + userId;
                        public final String status = "NONE";
                    }
            );
        }

        return ResponseEntity.ok(detailOpt.get());
    }

    @PutMapping("/pending/approve-all")
    public ResponseEntity<Map<String, Object>> approveAllPending() {
        List<ShopRequestsDto> approved = shopRequestService.approveAllPending();
        return ResponseEntity.ok(Map.of(
                "approvedCount", approved.size(),
                "items", approved
        ));
    }

    // Lấy tất cả shop đăng ký
    @GetMapping
    public List<ShopRequestsDto> getShopRequests() {
        return shopRequestService.getAllRequests();
    }

    // Lấy danh sách các yêu cầu đang chờ duyệt
    @GetMapping("/pending")
    public List<ShopRequestsDto> getPendingShopRequests() {
        return shopRequestService.getPendingRequests();
    }

    // Tạo yêu cầu mở shop mới
    @PostMapping("/create")
    public ResponseEntity<?> createShopRequest(@RequestBody ShopRequestsDto dto, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUser().getId();
        return shopRequestService.createRequest(userId, dto);
    }

    // Duyết yêu cầu
    @PostMapping("/{id}/approved")
    public ShopRequestsDto approveRequest(@PathVariable("id") Long requestId) {
        return shopRequestService.approveRequest(requestId);
    }

    // Từ chối yêu cầu
    @PostMapping("/{id}/rejected")
    public ShopRequestsDto rejectRequest(@PathVariable("id") Long requestId, @RequestBody String reason) {
        return shopRequestService.rejectRequest(requestId, reason);
    }
}
