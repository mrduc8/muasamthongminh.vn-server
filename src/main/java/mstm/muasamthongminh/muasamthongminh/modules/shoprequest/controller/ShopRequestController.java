package mstm.muasamthongminh.muasamthongminh.modules.shoprequest.controller;

import mstm.muasamthongminh.muasamthongminh.modules.auth.security.CustomUserDetails;
import mstm.muasamthongminh.muasamthongminh.modules.shoprequest.dto.ShopRequestsDto;
import mstm.muasamthongminh.muasamthongminh.modules.shoprequest.service.ShopRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shop-requests")
public class ShopRequestController {
    @Autowired
    private ShopRequestService shopRequestService;


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
