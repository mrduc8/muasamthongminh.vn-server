package mstm.muasamthongminh.muasamthongminh.modules.shoprequest.service;

import mstm.muasamthongminh.muasamthongminh.modules.shoprequest.dto.ShopRequestsDetailDto;
import mstm.muasamthongminh.muasamthongminh.modules.shoprequest.dto.ShopRequestsDto;
import mstm.muasamthongminh.muasamthongminh.modules.shoprequest.model.ShopRequests;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface ShopRequestService {
    // Tạo mới yêu cầu đăng ký shop từ ngươời đùng
    ResponseEntity<?> createRequest(Long userId, ShopRequestsDto dto);

    // Lấy tất cả danh sách các yêu cầu mở shop ở trạng thái "PENDING"
    List<ShopRequestsDto> getPendingRequests();

    // Lấy tất cả danh sách shop
    List<ShopRequestsDto> getAllRequests();

    // Phê duyệt một yêu cầu mở shop, chuyển trạng thái "APPROVED"
    ShopRequestsDto approveRequest(Long requestId);

    // Từ chối một yêu cầu mở shop, cập nhập trạng thái thành "REJECTED" và lưu lý do từ chối
    ShopRequestsDto rejectRequest(Long requestId, String reason);

    // Check trạng thái shop của user hiện tại
    Optional<ShopRequestsDto> getShopRequestUserId(Long userId);

    Optional<ShopRequestsDetailDto> getDetailShopRequest(Long userId);

    List<ShopRequestsDto> approveAllPending();

    ShopRequestsDetailDto updateDetail(Long userId, ShopRequestsDetailDto input);
}
