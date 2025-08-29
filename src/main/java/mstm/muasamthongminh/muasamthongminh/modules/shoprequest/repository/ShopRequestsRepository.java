package mstm.muasamthongminh.muasamthongminh.modules.shoprequest.repository;

import mstm.muasamthongminh.muasamthongminh.common.enums.ShopStatus;
import mstm.muasamthongminh.muasamthongminh.modules.auth.model.User;
import mstm.muasamthongminh.muasamthongminh.modules.shop.model.Shop;
import mstm.muasamthongminh.muasamthongminh.modules.shoprequest.model.ShopRequests;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopRequestsRepository extends JpaRepository<ShopRequests, Long> {
    //Lấy danh sách tất cả yêu cầu mở shop có trạng thái "PENDING"
    List<ShopRequests> findByStatus(ShopStatus shopStatus);

    Optional<ShopRequests> findByUserId(Long userId);

    // Kiểm tra xem user đã gửi yêu cầu chưa
    boolean existsByUserId(Long userId);

    // Xoá dữ liệu
    void deleteByUserId(Long userId);
}
